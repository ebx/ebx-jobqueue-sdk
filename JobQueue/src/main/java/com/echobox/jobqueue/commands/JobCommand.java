/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.echobox.jobqueue.commands;

import com.echobox.jobqueue.Job;
import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandCompletionListener;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.exceptions.JobQueueExceptionFormatter;
import com.echobox.jobqueue.status.JobCompletionStatus;
import com.echobox.jobqueue.status.JobFailedStatus;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.jobqueue.status.JobUnknownStatus;
import com.echobox.time.UnixTime;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Base command object for JobCommand implementations.  A JobCommand is an executable Job, which
 * contains the data and logic needed to perform a task.
 *
 * A JobCommand specifies execution behaviour by implementing the doExecute(...) method
 *
 * This method performs the work of executing or retrying the job,  and the default behaviour of
 * this method can be either synchronous or asynchronous.  Subclasses should ensure this default
 * execution mode is consistent with the return value of the isAsynchronousExecution() method.
 *
 * JobCommands can "complete" either successfully or unsuccessfully - until they have completed
 * their JobCompletionStatus is UNKNOWN.
 *
 * JobCommand implementations are responsible for setting their own completion status on 
 * execution.
 *
 * In many use-cases, job commands are required either to be workers ( WorkerJobCommand ),
 * or general job coordinators ( CoordinatorJobCommand ), or subtask coordinators
 * ( SubtaskCoordinatorJobCommand )
 *
 * WorkerJobCommands by default execute synchronously,  and set their completion status
 * according to whether they *doExecute* successfully.
 *
 * CoordinatorJobCommands execute either synchronously or asynchronously depending on
 * on the nature of the coordination with other jobs,  and they set their completion status
 * according to the dependent job(s) *completionStatus*
 *
 * SubtaskCoordinatorJobCommands are CoordinatorJobCommands that have been designed to manage
 * lists of subtasks of the same type
 *
 * Irrespective of the default behaviour of a job command, calling clients can choose whether
 * to executeSynchronously(...) - blocking until the job completes, or to
 * executeAsynchronously(...) - returning immediately a future handle to the job completion
 * status.
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * 
 * @author Michael Lavelle
 */
public abstract class JobCommand<C extends JobCommandExecutionContext<C, ?, ?>>
    implements Job, JobCommandCompletionListener {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * A logging instance for this class
   */
  private static Logger logger = LoggerFactory.getLogger(JobCommand.class);

  /**
   * Whether the job has been marked as completed, and if so at what time
   */
  @Property("completed_time")
  protected Long completedTimeUnix;

  /**
   * The unix time the job was run
   */
  @Property("execution_time")
  protected Long executionTimeUnix;

  /**
   * The message of any exception which caused this job to complete unsuccessfully, or
   * null if no exceptions yet occurred
   */
  @Property("exception_message")
  protected String exceptionMessage;

  /**
   * Any exception which caused this job to complete unsuccessfully, or 
   * null if no exceptions yet occurred
   */
  @Transient
  private transient Exception exception;

  /**
   * The unix time this job was created
   */
  @Property("creation_time")
  protected long jobCreationTimeUnix;

  /**
   * The type of this job
   */
  private JobType<?> jobCommandType;

  @Transient
  private transient List<JobCommandCompletionListener> completionListeners;

  /**
   * Instantiates a new Job command.
   *
   * @param jobCommandType the job command type
   * @param jobCreationTimeUnix the job creation time unix
   */
  public JobCommand(JobType<?> jobCommandType, long jobCreationTimeUnix) {
    this.jobCommandType = jobCommandType;
    this.jobCreationTimeUnix = jobCreationTimeUnix;
    this.completionListeners = new ArrayList<>();
  }

  /**
   * Add completion listener.
   *
   * @param completionListener the completion listener
   */
  public void addCompletionListener(JobCommandCompletionListener completionListener) {
    if (completionListeners == null) {
      this.completionListeners = new ArrayList<>();
    }
    this.completionListeners.add(completionListener);
  }

  /**
   * Is asynchronous execution boolean.
   *
   * @return Whether the implementation of the executeOrRetryMethod is by default asynchronous
   */
  protected abstract boolean isAsynchronousExecution();

  /**
   * Is completion status logging enabled boolean.
   *
   * @param jobCommandExecutionContext the job command execution context
   * @return Whether completion status logging is enable for this type of JobCommand
   */
  public boolean isCompletionStatusLoggingEnabled(C jobCommandExecutionContext) {
    return jobCommandExecutionContext.isLoggingEnabledForJobType(getJobType());
  }

  /**
   * Gets execution time unix.
   *
   * @return The executionTimeUnix
   */
  public Long getExecutionTimeUnix() {
    return executionTimeUnix;
  }

  /**
   *  This is a blocking request to execute this job command - blocking until the job is
   *  marked as completed, or until an exception is thrown during execution or wait for completion
   *
   * @param executionContext The context this job runs within
   * @param cleanupOnSuccess Whether to cleanup all resources used by this job on success
   * @return The JobSuccess The status returned by successful execution of this job
   * @throws InterruptedException the interrupted exception
   * @throws ExecutionException Non-timeout exception which caused the execution to be unsuccessful
   * @throws TimeoutException A timeout exception which caused the execution or  the wait for
   * completion to timeout
   */
  public final JobSuccess executeSynchronously(C executionContext, boolean cleanupOnSuccess)
      throws InterruptedException, ExecutionException, TimeoutException {

    Future<JobSuccess> futureCompletionStatus = null;
    try {
      futureCompletionStatus = executeOrRetry(executionContext, Integer.MAX_VALUE);
    } catch (TimeoutException e) {
      if (isCompletionStatusLoggingEnabled(executionContext)) {
        JobStatus determinedJobStatus = determineJobStatus();
        JobCommandEvent event =
            determinedJobStatus.hasCompletionError() ? JobCommandEvent.JOB_COMPLETED_UNSUCCESSFULLY
                : JobCommandEvent.JOB_ERROR;
        if (!executionContext.isShutdownRequested()) {
          logger.error(getLogMessage(executionContext, event,
              "TimeoutException thrown during synchronous execution - status:"
                  + determinedJobStatus), e);
        } else {
          logger.debug(getLogMessage(executionContext, event,
              "TimeoutException thrown during synchronous execution - status:"
                  + determinedJobStatus), e);
        }
      }
      throw e;
    } catch (Exception e) {
      if (isCompletionStatusLoggingEnabled(executionContext)) {
        logException(e, executionContext);
      }
      throw new ExecutionException(e);
    }
    JobSuccess completionStatus = null;
    try {
      completionStatus = futureCompletionStatus.get();
    } catch (InterruptedException | ExecutionException e) {
      logException(e, executionContext);
      throw e;
    }

    // Job will be complete without error at this point - that's what the future waits for
    // If the timeout has been reached and job not complete, a TimeoutException will have been
    // thrown, and ExecutionExceptions will be thrown for any execution errors which prevent
    // completion without error.

    if (cleanupOnSuccess) {

      try {
        cleanUp(executionContext);
      } catch (Exception e) {
        logger
            .error(getLogMessage(executionContext, JobCommandEvent.JOB_ERROR, "Failed to clean up"),
                e);
      }

    }

    return completionStatus;
  }
  
  private void logException(Exception exception, C executionContext) {
    JobStatus determinedJobStatus = determineJobStatus();
    JobCommandEvent event =
        determinedJobStatus.hasCompletionError() ? JobCommandEvent.JOB_COMPLETED_UNSUCCESSFULLY
            : JobCommandEvent.JOB_ERROR;
    if (!executionContext.isShutdownRequested()) {
      logger.error(getLogMessage(executionContext, event,
          "ExecutionException thrown during synchronous execution - status:"
              + determinedJobStatus), exception);
    } else {
      logger.debug(getLogMessage(executionContext, event,
          "ExecutionException thrown during synchronous execution - status:"
              + determinedJobStatus), exception);
    }
  } 

  /**
   *  Non-blocking method call which triggers execution of the JobCommand but does not wait for
   *  completion.   Failure to instigate the trigger will cause ExecutionExceptions to be thrown.
   *
   *  The returned {@code Future<JobSuccess>} handle can be used by clients to wait for completion
   *  and to be notified of exceptions which occur during execution.  When using the returned
   *  {@code Future<JobSuccess>},  clients can specify a custom timeout, or can use the default
   *  get() method of the Future to wait for the specified defaultMaxFutureWaitTimeoutSeconds
   *
   * @param executionContext the execution context
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that any
   *                                                   asynchronous parts of the execution graph
   *                                                   will wait for completion before timing out
   *                                                   - note that this does not apply to
   *                                                   synchronous parts of the execution graph
   * @return The future JobSuccess, if no exception is thrown during creation of the future
   * JobSuccess itself
   * @throws ExecutionException An exception indicating that it was not possible to  obtain the
   * future JobSuccess
   */
  public final Future<JobSuccess> executeAsynchronously(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws ExecutionException {

    try {
      if (isAsynchronousExecution()) {
        return executeOrRetry(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
      } else {
        JobCommand<C> asynchronousWrapper =
            new AsynchronousJobCommandAdapter<C, JobCommand<C>>(this,
                maxAsyncExecutionCompletionWaitTimeoutSecs);
        return asynchronousWrapper
            .executeOrRetry(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
      }
    } catch (TimeoutException e) {
      throw new ExecutionException(e);
    } catch (ExecutionException e) {
      throw e;
    } catch (Exception e) {
      throw new ExecutionException(e);
    }
  }

  /**
   * Executes or retries this job in the given execution context - implemented behaviour will either
   * be synchronous or asynchronous dependent on the implementation of the doExecute method,
   * under the condition that this behaviour is consistent with the isAsynchronousExecution()
   * method.
   *
   * If the job has already been completed when this method is called, the completion status
   * will be reset and the doExecute() method called again
   *
   * Details of any exceptions which caused unsuccessful completion can under normal operation
   * be found in the future JobSuccess.  Implementations can throw Exceptions
   * if there is a problem generating the JobSuccess.   Any TimeoutExceptions
   * thrown will indicate the the caller that the job has failed to due some timeout in the
   * execution graph
   *
   * @param executionContext the execution context
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that any
   *                                                   asynchronous parts of the execution graph
   *                                                   will wait for completion before timing out
   *                                                   - note that this does not apply to
   *                                                   synchronous parts of the execution graph
   * @return The future JobSuccess, if no exception is thrown during generating the future status
   * itself
   * @throws Exception Any exception thrown by the execution itself - which would indicate to
   * calling clients that the future JobSuccess was unsuccessful
   */
  protected final Future<JobSuccess> executeOrRetry(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {

    // Reset the completion status if this job has already been completed
    if (getJobCompletionStatus().isCompleted()) {
      resetCompletionStatus();
    }

    return execute(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
  }

  /**
   * Performs the work necessary to execute this job in the given execution context -
   * implemented behaviour can either be synchronous or asynchronous
   * under the condition that this behaviour is consistent with the isAsynchronousExecution()
   * method.
   *
   * This method assumes that the job status is not completed
   *
   * Details of any exceptions which caused unsuccessful completion can under normal operation
   * be found in the future JobSuccess.  Implementations can throw Exceptions
   * if there is a problem generating the JobSuccess.   Any TimeoutExceptions
   * thrown will indicate the the caller that the job has failed to due some timeout in the
   * execution graph
   *
   * @param executionContext the execution context
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that any
   *                                                   asynchronous parts of the execution graph
   *                                                   will wait for completion before timing out
   *                                                   - note that this does not apply to
   *                                                   synchronous parts of the execution graph
   * @return The future JobSuccess, if no exception is thrown during generating the future status
   * itself
   * @throws Exception Any exception thrown by the execution itself - which would indicate to
   * calling clients that the future JobSuccess was unsuccessful
   */
  protected abstract Future<JobSuccess> doExecute(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception;

  /**
   * Performs the work necessary to execute this job in the given execution context -
   * implemented behaviour can either be synchronous or asynchronous depending on the implementation
   * of the doExecuteMethod, under the condition that this behaviour is consistent with the
   * isAsynchronousExecution()  method.
   *
   * This method performs a check to ensure that the job is not completed before calling
   * the doExecute method - an IllegalStateException will be thrown if this is not the case
   *
   * Implementations will throw Exceptions if there is a problem generating the
   * JobSuccess.   Any TimeoutExceptions  thrown will indicate the the caller that the
   * job has failed to due some timeout in the execution graph
   *
   * @param executionContext the execution context
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that any
   *                                                   asynchronous parts of the execution graph
   *                                                   will wait for completion before timing out
   *                                                   - note that this does not apply to
   *                                                   synchronous parts of the execution graph
   * @return The future JobSuccess, if no exception is thrown during generating the future status
   * itself
   * @throws Exception Any exception thrown by the execution itself - which would indicate to
   * calling clients that the future JobSuccess was unsuccessful
   */
  public final Future<JobSuccess> execute(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {
    this.executionTimeUnix = UnixTime.now();
    return doExecute(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
  }

  /**
   * Performs any cleanup operations for this job in the given execution context
   *
   * @param executionContext the execution context
   * @throws Exception the exception
   */
  public abstract void cleanUp(C executionContext) throws Exception;

  /**
   * Gets exception.
   *
   * @return Any exception which caused this job to complete unsuccessfully, or  null if no
   * exceptions yet occurred
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Gets exception message.
   *
   * @return The exception message
   */
  public String getExceptionMessage() {
    return exceptionMessage;
  }

  /**
   * The job command runtime status 
   * @return The job command runtime status
   */
  public abstract JobStatus determineJobStatus();

  /**
   * Gets the current completion status
   * @return The current completion status
   */
  public JobCompletionStatus getJobCompletionStatus() {
    boolean isCompleted = getCompletedUnixTime() != null;
    boolean hasExceptionOccurred = getException() != null;
    if (isCompleted) {
      if (hasExceptionOccurred) {
        return new JobFailedStatus(getException());
      } else {
        return new JobSuccess();
      }
    } else {
      return new JobUnknownStatus();
    }
  }

  /**
   * Gets completed unix time.
   *
   * @return The completedUnixTime
   */
  public Long getCompletedUnixTime() {
    return completedTimeUnix;
  }

  /**
   * Sets the completedUnixTime
   * @param completedUnixTime the completedUnixTime to set
   */
  public final void setSuccessfulCompletionUnixTime(long completedUnixTime) {

    if (completionListeners != null) {
      for (JobCommandCompletionListener completionListener : completionListeners) {
        completionListener.setSuccessfulCompletionUnixTime(completedUnixTime);
      }
    }
    this.completedTimeUnix = completedUnixTime;
  }

  /**
   * Resets the completion status of this job only ( this does not affect the completion status
   * of any dependent jobs)
   */
  public void resetCompletionStatus() {
    this.completedTimeUnix = null;
    this.exception = null;
    this.exceptionMessage = null;
    this.jobCreationTimeUnix = UnixTime.now();
    if (completionListeners != null) {
      for (JobCommandCompletionListener completionListener : completionListeners) {
        completionListener.resetCompletionStatus();
      }
    }
  }

  /**
   * Sets the unsuccessful completion unix time 
   * @param completedUnixTime the completedUnixTime to set
   * @param exception exception
   */
  public final void setUnsuccessfulCompletionUnixTime(long completedUnixTime, Exception exception) {
    this.completedTimeUnix = completedUnixTime;
    this.exception = exception;
    this.exceptionMessage = exception == null ? null
        : ((exception.getMessage() == null ? "" : (exception.getMessage() + ":")))
            + JobQueueExceptionFormatter.getStackTraceString(exception);
    if (completionListeners != null) {
      for (JobCommandCompletionListener completionListener : completionListeners) {
        completionListener.setUnsuccessfulCompletionUnixTime(completedUnixTime, exception);
      }
    }
  }

  /**
   * Gets the job type
   * @return The jobType
   */
  public JobType<?> getJobType() {
    return jobCommandType;
  }

  /**
   * Gets the jobCreationTimeUnix
   * @return The jobCreationTimeUnix
   */
  public long getJobCreationTimeUnix() {
    return jobCreationTimeUnix;
  }

  /**
   * Gets log message.
   *
   * @param executionContext the execution context
   * @param jobCommandEvent the job command event
   * @param message the message
   * @return A formatted logging messages for events that occur in the context of executing  this
   * job command
   */
  public String getLogMessage(C executionContext, JobCommandEvent jobCommandEvent, String message) {
    return executionContext.toString() + " - " + jobCommandEvent + " - " + toString() + " - "
        + message;
  }

  /**
   * Create interrupt strategy job command interruption strategy.
   *
   * @return The interruption strategy
   */
  public abstract JobCommandInterruptionStrategy createInterruptStrategy();

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ":" + getJobType();
  }

  /**
   * Gets progress report.
   *
   * @return The job progress report
   */
  public abstract JobProgressReport getProgressReport();
  
  /**
   * Obtain a map containing logging info we can log as structured entities for a JobCommand
   * 
   * By default this map contains fields inspected via reflection and the job type enum
   * 
   * @param loggingKeysByType 
   * @return The logging info map - empty by default.
   */
  public Map<String, Object> getLoggingInfoMap(Map<Class<?>, String> loggingKeysByType) {

    Map<String, Object> loggingInfoMap = new HashMap<>();
    
    try {

      Enum<?> jobTypeEnum = getJobType().getJobTypeEnum();
      if (jobTypeEnum == null) {
        logger.error("Job type is not set for " + getClass().getSimpleName());
      } else {
        String jobTypeEnumLoggingKey = loggingKeysByType.get(jobTypeEnum.getClass());

        if (jobTypeEnumLoggingKey != null) {
          loggingInfoMap.put(jobTypeEnumLoggingKey, jobTypeEnum);
        }
      }

      Arrays.stream(this.getClass().getDeclaredFields())
          .filter(field -> {
            final int modifiers = field.getModifiers();
            return !(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
          })
          .forEach(field -> {
            Class<?> type = field.getType();
            Object value = null;
            try {
              field.setAccessible(true);
              value = field.get(this);
            } catch (IllegalAccessException e) {
              logger.warn("Unable to access field", e);
            }
  
            if (value != null && loggingKeysByType.keySet().contains(type)) {
    
              String fieldName = loggingKeysByType.get(type);
              fieldName = fieldName == null ? field.getName() : fieldName;
              loggingInfoMap.put(fieldName, value);
            }
          });
    } catch (Exception e) {
      logger.error("Exception thrown from getLoggingInfoMap method:", e);
    }
    return loggingInfoMap;
  }
}
