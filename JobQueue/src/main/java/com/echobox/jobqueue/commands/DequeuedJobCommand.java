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

import com.echobox.jobqueue.JobOnQueue;
import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.QueuedJobCompletionUpdater;
import com.echobox.jobqueue.status.JobCompletionStatus;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatusType;
import com.echobox.jobqueue.status.JobSuccess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * JobCommand coordinator which manages the dequeued JobCommand being executed by a consumer
 * and the associated JobCommand on the queue.
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public class DequeuedJobCommand<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable>
    extends SingleTaskCoordinatorJobCommand<C, JobCommand<C>>
    implements JobOnQueue<Q, I, JobCommand<C>> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  /**
   * The key we use to log the time from queue to completion
   */
  private static final String QUEUE_TO_COMPLETION_TIME_LOGGING_KEY = "queueToCompletionSeconds";
  private static final String COMMAND_RUN_TIME_LOGGING_KEY = "commandRunTimeSeconds";
  
  private QueuedJobId<Q, I> queuedJobId;
  private PersistentQueuedJobCommandQueue<C, Q, I> jobQueue;
  private String consumerId;

  /**
   * Instantiates a new Dequeued job command.
   *
   * @param jobQueue The job queue we are consuming from
   * @param jobCommand The job we wish to execute
   * @param jobCreationTimeUnix the job creation time unix
   * @param queuedJobId the queued job id
   * @param consumerId the consumer id
   */
  public DequeuedJobCommand(PersistentQueuedJobCommandQueue<C, Q, I> jobQueue, 
          JobCommand<C> jobCommand,
      long jobCreationTimeUnix, QueuedJobId<Q, I> queuedJobId, String consumerId) {
    super(jobCommand, jobCreationTimeUnix);
    this.jobQueue = jobQueue;
    this.queuedJobId = queuedJobId;
    this.consumerId = consumerId;
    this.addCompletionListener(
        new QueuedJobCompletionUpdater<C, Q, I>(jobQueue, consumerId, queuedJobId));
  }

  /**
   * Gets queued job id.
   *
   * @return The queue-type indicator and unique job id which identify this job on the queue
   */
  public QueuedJobId<Q, I> getQueuedJobId() {
    return queuedJobId;
  }

  /**
   * Update heartbeat time boolean.
   *
   * @param heartbeatUnixTime The unix time of the heartbeat to set for the QueuedJob on the
   *                          JobCommandQueue
   * @return Whether the heartbeat update was successful
   */
  public boolean updateHeartbeatTime(long heartbeatUnixTime) {
    return jobQueue.updateHeartbeatTime(queuedJobId, consumerId, heartbeatUnixTime);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#getException()
   */
  @Override
  public Exception getException() {
    Exception exception = super.getException();
    if (exception == null && this.exceptionMessage != null) {
      exception = new RuntimeException(exceptionMessage);
    }
    return exception;
  }

  /**
   * Gets queued job.
   *
   * @return The JobCommand we are queueing
   */
  public JobCommand<C> getQueuedJob() {
    return jobCommand;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.JobCommand#doExecute(
   * com.echobox.jobqueue.context.JobCommandExecutionContext, long)
   */
  @Override
  protected Future<JobSuccess> doExecute(C executionContext,
      long defaultMaxFutureWaitTimeoutSeconds) throws Exception {
    return jobCommand.executeOrRetry(executionContext, defaultMaxFutureWaitTimeoutSeconds);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DequeuedJobCommand [queuedJob=" + jobCommand + ", queuedJobId=" + queuedJobId + "]";
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isAsynchronousExecution()
   */
  @Override
  protected boolean isAsynchronousExecution() {
    return jobCommand.isAsynchronousExecution();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#createInterruptStrategy()
   */
  @Override
  public JobCommandInterruptionStrategy createInterruptStrategy() {
    return jobCommand.createInterruptStrategy();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.SingleTaskCoordinatorJobCommand#
   * isCompletionStatusLoggingEnabled(com.echobox.jobqueue.JobCommandExecutionContext)
   */
  @Override
  public boolean isCompletionStatusLoggingEnabled(C executionContext) {
    return executionContext.isLoggingEnabledForJobType(this.getJobType());
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.QueuedJob#getId()
   */
  @Override
  public QueuedJobId<Q, I> getId() {
    return queuedJobId;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.QueuedJob#getJob()
   */

  /**
   * Get the job
   * @return JobCommand
   */
  public JobCommand<C> getJob() {
    return this.getQueuedJob();
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.JobCommand#getProgressReport()
   */
  @Override
  public JobProgressReport getProgressReport() {
    return jobCommand.getProgressReport();
  }
  
  @Override
  public Map<String, Object> getLoggingInfoMap(Map<Class<?>, String> loggingKeysByType) {

    Map<String, Object> loggingInfoMap = new HashMap<>();
    // Add the wrapped job command's logging info to the map
    loggingInfoMap.putAll(jobCommand.getLoggingInfoMap(loggingKeysByType));

    // Obtain the queue type and the logging key
    Q queueType = getId().getQueueType();
    String queueTypeLoggingKey = loggingKeysByType.get(queueType.getClass());

    if (queueType != null && queueTypeLoggingKey != null) {
      loggingInfoMap.put(queueTypeLoggingKey, queueType);
    }

    // Obtain the status type and the logging key
    JobCompletionStatus completionStatus = jobCommand.getJobCompletionStatus();
    if (completionStatus != null) {
      JobStatusType completionStatusType = jobCommand.getJobCompletionStatus().getStatusType();
      String completionStatusLoggingKey = loggingKeysByType.get(completionStatusType.getClass());

      if (completionStatusType != null && completionStatusLoggingKey != null) {
        loggingInfoMap.put(completionStatusLoggingKey, completionStatusType);
      }
      if (completionStatus.isCompleted()) {

        Long completionUnixTime = jobCommand.getCompletedUnixTime();
        if (completionUnixTime != null) {
          long queuedUnixTime = this.getJobCreationTimeUnix();
          long queuedToCompletionTime = completionUnixTime - queuedUnixTime;
          loggingInfoMap.put(QUEUE_TO_COMPLETION_TIME_LOGGING_KEY, queuedToCompletionTime);
        }
        Long executionTimeUnix = jobCommand.getExecutionTimeUnix();
        if (executionTimeUnix != null && completionUnixTime != null) {
          long jobRunTimeSeconds = completionUnixTime - executionTimeUnix;
          loggingInfoMap.put(COMMAND_RUN_TIME_LOGGING_KEY, jobRunTimeSeconds);
        }
      }
    }

    return loggingInfoMap;
  }
}
