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
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.status.JobCompletionStatus;
import com.echobox.jobqueue.status.JobFailedStatus;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.time.UnixTime;

/**
 * CoordinatorJobCommands can execute either synchronously or asynchronously depending on
 * on the nature of the coordination with other jobs,  and they determine their completion status
 * according to the dependent job(s) *completionStatus*
 *
 * @param <C>  The type of JobCommandExecutionContext in which we execute commands
 * 
 * @author Michael Lavelle
 */
public abstract class CoordinatorJobCommand<C extends JobCommandExecutionContext<C, ?, ?>>
    extends JobCommand<C> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new Coordinator job command.
   *
   * @param jobType the job type
   * @param jobCreationTimeUnix the job creation time unix
   */
  public CoordinatorJobCommand(JobType<?> jobType, long jobCreationTimeUnix) {
    super(jobType, jobCreationTimeUnix);
  }

  /**
   * Gets delegated status.
   *
   * @return The completion status as defined by the jobs being coordinated
   */
  protected abstract JobStatus getDelegatedStatus();

  /**
   * Determines the status
   * @return The status
   */
  public final JobStatus determineJobStatus() {

    boolean interruptOnPreCompletionWarning =
        createInterruptStrategy().interruptOnPreCompletionWarning();

    boolean completeWithErrorOnInterruption =
        createInterruptStrategy().completeWithErrorOnInterruption();

    JobStatus status = null;
    JobCompletionStatus completionStatus = getJobCompletionStatus();
    status = completionStatus;
    if (!completionStatus.isCompleted()) {
      JobStatus delegatedCompletionStatus = getDelegatedStatus();
      if (delegatedCompletionStatus != null) {

        if (delegatedCompletionStatus.isCompleted()) {
          if (delegatedCompletionStatus.isCompletedWithoutError()) {
            setSuccessfulCompletionUnixTime(UnixTime.now());
          } else {
            setUnsuccessfulCompletionUnixTime(UnixTime.now(),
                delegatedCompletionStatus.getException());
          }
          status = delegatedCompletionStatus;

        } else if (interruptOnPreCompletionWarning && delegatedCompletionStatus
            .hasPreCompletionWarning() && completeWithErrorOnInterruption) {

          JobCompletionStatus updatedStatus =
              new JobFailedStatus(delegatedCompletionStatus.getException());

          setUnsuccessfulCompletionUnixTime(UnixTime.now(), updatedStatus.getException());

          status = updatedStatus;

        } else if (interruptOnPreCompletionWarning && delegatedCompletionStatus
            .hasPreCompletionWarning() && !completeWithErrorOnInterruption) {
          status = delegatedCompletionStatus;

        }
      }
    }
    return status;
  }
}
