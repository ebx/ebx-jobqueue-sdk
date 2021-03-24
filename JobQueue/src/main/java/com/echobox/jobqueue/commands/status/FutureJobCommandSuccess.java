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

package com.echobox.jobqueue.commands.status;

import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobCompletionStatus;
import com.echobox.jobqueue.status.JobFailedStatus;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.time.UnixTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of Future for JobSuccess we can return from methods responsible for the
 * asynchronous execution of JobCommands.  Delegates to the executing job command's status
 * methods.
 *
 * @param <C> The type of JobCommandExecutionContext we execute JobCommands within
 * 
 * @author Michael Lavelle
 */
public class FutureJobCommandSuccess<C extends JobCommandExecutionContext<C, ?, ?>>
    implements Future<JobSuccess> {

  /**
   * A logging instance for this class
   */
  private static Logger logger = LoggerFactory.getLogger(FutureJobCommandSuccess.class);

  private JobCommand<C> jobCommand;
  private long maxAsyncExecutionCompletionWaitTimeoutSecs;
  private C context;

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#cancel(boolean)
   */
  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    throw new UnsupportedOperationException("Cancellation not yet supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#isCancelled()
   */
  @Override
  public boolean isCancelled() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#isDone()
   */
  @Override
  public boolean isDone() {
    JobStatus completionStatus = jobCommand.determineJobStatus();
    return completionStatus.isCompleted();
  }

  /**
   * Instantiates a new Future job command success.
   *
   * @param jobCommand the job command
   * @param context the context
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that any
   *                                                   asynchronous parts of the execution graph
   *                                                   will wait for completion before timing out
   *                                                   - note that this does not apply to
   *                                                   synchronous parts of the execution graph
   */
  public FutureJobCommandSuccess(JobCommand<C> jobCommand, C context,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) {
    super();
    this.jobCommand = jobCommand;
    this.maxAsyncExecutionCompletionWaitTimeoutSecs = maxAsyncExecutionCompletionWaitTimeoutSecs;
    this.context = context;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#get()
   */
  @Override
  public JobSuccess get() throws InterruptedException, ExecutionException {
    try {
      awaitCompletion(maxAsyncExecutionCompletionWaitTimeoutSecs,
          jobCommand.createInterruptStrategy());

    } catch (TimeoutException e) {
      ExecutionException executionException = new ExecutionException(e);
      throw executionException;
    }
    JobStatus jobCommandStatus = jobCommand.determineJobStatus();
    if (!jobCommandStatus.isCompleted()) {
      throw new IllegalStateException("Job should be completed at this point");

    } else {
      if (jobCommandStatus.isCompletedWithoutError()) {
        return new JobSuccess();
      } else {
        throw new ExecutionException(jobCommandStatus.getException());

      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
   */
  @Override
  public JobSuccess get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {

    long timeInSeconds = TimeUnit.SECONDS.convert(timeout, unit);

    if (jobCommand.isCompletionStatusLoggingEnabled(context)) {
      logger.trace(
          jobCommand.getLogMessage(context, JobCommandEvent.JOB_INFO, "Waiting for completion"));
    }
    awaitCompletion(timeInSeconds, jobCommand.createInterruptStrategy());
    JobStatus completionStatus = jobCommand.determineJobStatus();
    if (!completionStatus.isCompleted()) {
      throw new IllegalStateException("Expected job to be completed!");
    } else {
      if (completionStatus.isCompletedWithoutError()) {
        if (jobCommand.isCompletionStatusLoggingEnabled(context)) {
          logger.trace(jobCommand.getLogMessage(context, JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY,
              "Successful Completion"));
        }
        return new JobSuccess();

      } else {
        if (jobCommand.isCompletionStatusLoggingEnabled(context)) {
          logger.trace(jobCommand
              .getLogMessage(context, JobCommandEvent.JOB_COMPLETED_UNSUCCESSFULLY,
                  "Unsuccessful Completion"), completionStatus.getException());
        }
        throw new ExecutionException(completionStatus.getException());
      }
    }
  }

  /**
   * Wait for completion of the job for timeoutSeconds
   *
   * @param maxTimeoutSeconds the max timeout seconds
   * @param interruptStrategy the interrupt strategy
   * @return A JobCompletionStatus Future
   * @throws InterruptedException the interrupted exception
   * @throws TimeoutException the timeout exception
   * @throws ExecutionException the execution exception
   */
  public JobCompletionStatus awaitCompletion(long maxTimeoutSeconds,
      JobCommandInterruptionStrategy interruptStrategy)
      throws InterruptedException, TimeoutException, ExecutionException {

    long startTime = UnixTime.now();
    long timeoutTime = startTime + maxTimeoutSeconds;
    boolean timedOut = false;

    boolean completeWithErrorOnInterruption = interruptStrategy.completeWithErrorOnInterruption();

    boolean interruptOnPreCompletionWarning = interruptStrategy.interruptOnPreCompletionWarning();

    JobStatus status = jobCommand.determineJobStatus();
    while (!status.isCompleted() && !timedOut) {

      if (context.isShutdownRequested()) {
        throw new InterruptedException("Application shutdown has been requested");
      }

      // If this job completes with error on interruption, this loop will terminate
      // by nature of the job completing.

      // For the case where the job does not complete with error on interruption, we
      // may wish to stop waiting if interruptOnPreCompletionWarning is true and
      // if there is a pre-completion warning

      if (!completeWithErrorOnInterruption && interruptOnPreCompletionWarning && status
          .hasPreCompletionWarning()) {
        throw new ExecutionException(status.getException());
      }

      // Check for timeout, and stop waiting if a timeout occurs
      if (UnixTime.now() >= timeoutTime) {
        timedOut = true;
      }
      if (timedOut) {
        if (jobCommand.isCompletionStatusLoggingEnabled(context)) {
          logger.trace(jobCommand.getLogMessage(context, JobCommandEvent.JOB_ERROR,
              "Timed out waiting for job to complete"));
        }
        throw new TimeoutException("Timed out waiting for job:" + this.jobCommand + " to complete");
      } else {
        Thread.sleep(1000);
      }
      status = jobCommand.determineJobStatus();
    }
    // Sanity check
    if (!status.isCompleted()) {
      throw new IllegalStateException("Job should be completed at this point");
    } else {
      // Return success or failure, now that job has completed

      if (status.hasCompletionError()) {
        return new JobFailedStatus(status.getException());
      } else {
        return new JobSuccess();
      }
    }
  }
}
