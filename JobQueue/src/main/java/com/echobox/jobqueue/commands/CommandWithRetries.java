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

import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.commands.status.FutureJobCommandSuccess;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.time.UnixTime;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Command with retries.
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <J> The type of JobCommand we are adapting
 *
 * @author Michael Lavelle
 */
public class CommandWithRetries<C extends JobCommandExecutionContext<C, ?, ?>, J
    extends JobCommand<C>>
    extends SingleTaskCoordinatorJobCommand<C, J> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * A logging instance for this instance
   */
  private Logger logger;

  private long attemptTimeoutSeconds;
  private Integer maxRetries;
  private int currentRetryAttempts = 0;
  private boolean logAtInfoLevel;

  /**
   * Instantiates a new Command with retries.
   *
   * @param jobCommand the job command
   * @param attemptTimeoutSeconds the attempt timeout seconds
   * @param maxRetries the max retries
   * @param logger The logger to use for this instance
   * @param logAtInfoLevel the log at info level
   */
  public CommandWithRetries(J jobCommand, long attemptTimeoutSeconds, Integer maxRetries,
      Logger logger, boolean logAtInfoLevel) {
    super(jobCommand, jobCommand.getJobCreationTimeUnix());
    this.attemptTimeoutSeconds = attemptTimeoutSeconds;
    this.maxRetries = maxRetries;
    this.logger = logger;
    this.logAtInfoLevel = logAtInfoLevel;
  }

  private boolean isInfiniteTrigger() {
    return maxRetries == null;
  }
  
  @Override
  protected Future<JobSuccess> doExecute(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {

    currentRetryAttempts = 0;

    long startTime = UnixTime.now();

    boolean firstAttempt = true;
    
    while ((firstAttempt || !jobCommand.determineJobStatus().isCompletedWithoutError())
        && !executionContext.isShutdownRequested() && (isInfiniteTrigger()
        || currentRetryAttempts < maxRetries)) {

      if (logAtInfoLevel) {
        logger.info(getLogMessage(executionContext, JobCommandEvent.JOB_STARTED,
            "Executing job, waiting up to:" + attemptTimeoutSeconds
                + " seconds for completion with current #retries:" + currentRetryAttempts));
      } else {
        logger.debug(getLogMessage(executionContext, JobCommandEvent.JOB_STARTED,
            "Executing job, waiting up to:" + attemptTimeoutSeconds
                + " seconds for completion with current #retries:" + currentRetryAttempts));
      }
      // Execute the job asynchronously and wait for completion for up to attemptTimeoutSeconds

      try {

        Future<JobSuccess> completionStatusFuture =
            jobCommand.executeAsynchronously(executionContext, attemptTimeoutSeconds);

        JobSuccess completionStatus =
            completionStatusFuture.get(attemptTimeoutSeconds, TimeUnit.SECONDS);

        if (completionStatus.isCompletedWithoutError()) {
          if (logAtInfoLevel) {
            logger.info(jobCommand
                .getLogMessage(executionContext, JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY,
                    "Successful completion:" + jobCommand + " in " + getDurationSince(startTime)
                        + " seconds with retries:" + currentRetryAttempts), StructuredArguments
                .entries(jobCommand.getProgressReport().getProgressInformation()));
          } else {
            logger.debug(jobCommand
                .getLogMessage(executionContext, JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY,
                    "Successful completion:" + jobCommand + " in " + getDurationSince(startTime)
                        + " seconds with retries:" + currentRetryAttempts), StructuredArguments
                .entries(jobCommand.getProgressReport().getProgressInformation()));
          }
        } else {
          throw new IllegalStateException(
              "Unexpected error " + "- the job should be successfully completed at this point",
              completionStatus.getException());
        }

      } catch (TimeoutException exception) {
        // Catch exceptions, and increment retry attempts, before attempting again
        // if we haven't exceeded retry attempts, or re-throwing exception if we have
        // exceeded max attempts. This is an expected error, so we pass in "false" here
        // to modify logging behaviour
        handleExceptionAndIncrementRetryCounter(executionContext, startTime, exception,
            "due to to timeout", false);
      } catch (Exception exception) {
        // Catch exceptions, and increment retry attempts, before attempting again
        // if we haven't exceeded retry attempts, or re-throwing exception if we have
        // exceeded max attempts. This is an expected error, so we pass in "true" here
        // to modify logging behaviour
        handleExceptionAndIncrementRetryCounter(executionContext, startTime, exception,
            "due to exception", true);
      }
      firstAttempt = false;
    }

    return new FutureJobCommandSuccess<>(this, executionContext,
        attemptTimeoutSeconds);

  }

  private <E extends Exception> void handleExceptionAndIncrementRetryCounter(
      C executionContext, long startTime, E exception, String reason,
      boolean unexpected) throws E {

    JobStatus completionStatus = jobCommand.getJobCompletionStatus();

    if (completionStatus.isCompletedWithoutError()) {
      throw new IllegalStateException(
          "Job should not be completed successfully if an exception has been thrown");
    }

    Map<String, Object> progressReport = jobCommand.getProgressReport().getProgressInformation();
    if (!progressReport.isEmpty()) {
      logger.debug(jobCommand.getLogMessage(executionContext, JobCommandEvent.JOB_INFO,
          "Completed wait for completion of :" + jobCommand + " " + reason + " in "
              + getDurationSince(startTime) + " seconds with retries:" + currentRetryAttempts),
          StructuredArguments.entries(progressReport));
    }
    if (unexpected) {
      logger.warn(jobCommand.getLogMessage(executionContext, JobCommandEvent.JOB_ERROR,
          "Completed wait " + "for completion of :" + jobCommand + " " + reason + " in "
              + getDurationSince(startTime) + " seconds with retries:" + currentRetryAttempts),
          exception);
    }
    currentRetryAttempts++;
    if (!isInfiniteTrigger() && currentRetryAttempts >= maxRetries) {

      // Job is completed with error. If it hasn't already been marked as completed,
      // mark it as uncompleted here
      if (!jobCommand.getJobCompletionStatus().isCompletedWithError()) {
        jobCommand.setUnsuccessfulCompletionUnixTime(UnixTime.now(), exception);
        logger.error(jobCommand
            .getLogMessage(executionContext, JobCommandEvent.JOB_COMPLETED_UNSUCCESSFULLY,
                "Set job to be unsuccessfully completed " + " :" + jobCommand + reason + " in "
                    + getDurationSince(startTime) + " seconds with retries:"
                    + currentRetryAttempts), exception);
      }
      throw exception;
    }
  }
  
  @Override
  public JobCommandInterruptionStrategy createInterruptStrategy() {
    return JobCommandInterruptionStrategy.NONE;
  }
  
  @Override
  public JobProgressReport getProgressReport() {
    return jobCommand.getProgressReport();
  }

  /**
   * Gets current retry attempts.
   *
   * @return currentRetryAttempts current retry attempts
   */
  public int getCurrentRetryAttempts() {
    return currentRetryAttempts;
  }

  private long getDurationSince(long startTime) {
    long endTime = UnixTime.now();
    long duration = endTime - startTime;
    return duration;
  }
}
