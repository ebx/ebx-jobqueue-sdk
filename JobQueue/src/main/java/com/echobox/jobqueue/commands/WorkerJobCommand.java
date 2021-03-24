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

import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.commands.status.FutureJobCommandSuccess;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.jobqueue.status.ProgressStatsUnavailable;
import com.echobox.time.UnixTime;
import org.slf4j.Logger;

import java.util.concurrent.Future;

/**
 * WorkerJobCommands by default execute synchronously,  and set their completion status
 * according to whether they *doWork* successfully.
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * 
 * @author Michael Lavelle
 */
public abstract class WorkerJobCommand<C extends JobCommandExecutionContext<C, ?, ?>>
    extends JobCommand<C> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new Worker job command.
   *
   * @param jobType The type of this Job
   * @param jobCreationTimeUnix The unix time when the job was created
   */
  public WorkerJobCommand(JobType<?> jobType, long jobCreationTimeUnix) {
    super(jobType, jobCreationTimeUnix);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isAsynchronousExecution()
   */
  @Override
  protected boolean isAsynchronousExecution() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#doExecute(com.echobox.jobqueue.
   * JobCommandExecutionContext,
   * long)
   */
  @Override
  protected Future<JobSuccess> doExecute(C executionContext,
      long defaultMaxFutureWaitTimeoutSeconds) throws Exception {

    try {
      if (isCompletionStatusLoggingEnabled(executionContext)) {
        getLogger()
            .debug(getLogMessage(executionContext, JobCommandEvent.JOB_STARTED, "Executing job"));
      }

      doWork(executionContext);

      if (isCompletionStatusLoggingEnabled(executionContext)) {

        getLogger().debug(
            getLogMessage(executionContext, JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY,
                "Job Completed Successfully"));
      }

      setSuccessfulCompletionUnixTime(UnixTime.now());

      return new FutureJobCommandSuccess<C>(this, executionContext,
          defaultMaxFutureWaitTimeoutSeconds);
    } catch (Exception e) {
      getLogger().error(
          getLogMessage(executionContext, JobCommandEvent.JOB_COMPLETED_UNSUCCESSFULLY,
              "Job Completed Unsuccessfully"), e);
      setUnsuccessfulCompletionUnixTime(UnixTime.now(), e);
      throw e;
    }
  }

  /**
   * Do work.
   *
   * @param executionContext The execution context we perform the work within
   * @throws Exception Any exception thrown performing the work
   */
  protected abstract void doWork(C executionContext) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#cleanUp(com.echobox.jobqueue.context.
   * JobCommandExecutionContext)
   */
  @Override
  public void cleanUp(C executionContext) throws Exception {
    // No-op by default
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#createInterruptStrategy()
   */
  @Override
  public JobCommandInterruptionStrategy createInterruptStrategy() {
    return JobCommandInterruptionStrategy.NONE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#determineJobStatus()
   */
  @Override
  public final JobStatus determineJobStatus() {
    return getJobCompletionStatus();
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.JobCommand#getProgressReport()
   */
  @Override
  public JobProgressReport getProgressReport() {
    return new ProgressStatsUnavailable();
  }

  /**
   * Gets logger.
   *
   * @return a logger for the specific subclass of WorkerJobCommand
   */
  protected abstract Logger getLogger();
}
