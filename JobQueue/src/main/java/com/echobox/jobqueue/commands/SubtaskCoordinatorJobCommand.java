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
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.IfRetriedBeforeCompletion;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.RaiseWarning;
import com.echobox.jobqueue.commands.status.FutureJobCommandSuccess;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobCompletionStatus;
import com.echobox.jobqueue.status.JobFailedStatus;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.jobqueue.status.JobUnknownStatus;
import com.echobox.jobqueue.status.JobWarningStatus;
import com.echobox.jobqueue.status.ProgressStatsUnavailable;
import com.echobox.jobqueue.status.SubtaskCoordinatorStats;
import com.echobox.time.UnixTime;
import org.mongodb.morphia.annotations.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Future;

/**
 * SubtaskCoordinatorJobCommands are CoordinatorJobCommands that have been designed to manage 
 * lists of subtasks of the same type
 *
 * @author Michael Lavelle
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <J> The type of JobCommand of each subtask
 */
public abstract class SubtaskCoordinatorJobCommand<C extends JobCommandExecutionContext<C, ?, ?>,
    J extends JobCommand<C>>
    extends CoordinatorJobCommand<C> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * A logging instance for this class
   */
  private static Logger logger = LoggerFactory.getLogger(SubtaskCoordinatorJobCommand.class);

  /**
   * The subtasks
   */
  @Transient
  protected transient List<J> jobCommands;

  /**
   * Gets the sub-tasks
   * @return jobCommands
   */
  public List<J> getSubtasks() {
    return jobCommands;
  }

  /**
   * The desired behaviour for this subtask coordinator
   * @return The desired behaviour for this subtask coordinator
   */
  public abstract SubtaskCoordinatorBehaviour createSubtaskCoordinatorBehaviour();

  /**
   * The constructor
   * @param jobCommandType The type of this Job
   * @param jobCreationTimeUnix The unix time when the job was created
   */
  protected SubtaskCoordinatorJobCommand(JobType<?> jobCommandType, long jobCreationTimeUnix) {
    super(jobCommandType, jobCreationTimeUnix);
  }

  /**
   * Provides an opportunity for implementing classes to update the list subtasks before the job
   * is retried
   *
   * @param executionContext The context we are updating the subtasks in
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that
   * any asynchronous parts of the execution graph will wait for completion before
   * timing out - note that this does not apply to synchronous parts of the execution graph
   * @throws Exception Any exception occurring during the subtask update process
   */
  protected abstract void updateSubtasksBeforeRetry(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#cleanUp(
   * com.echobox.jobqueue.context.JobCommandExecutionContext)
   */
  @Override
  public void cleanUp(C executionContext) throws Exception {
    logger.trace(getLogMessage(executionContext, JobCommandEvent.JOB_INFO, "Cleaning up command"));
    if (jobCommands != null) {
      for (J jobCommand : jobCommands) {
        jobCommand.cleanUp(executionContext);
      }
    }
  }

  /**
   * Create sub-tasks
   * @param executionContext The context we are creating the subtasks in
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that
   * any asynchronous parts of the execution graph will wait for completion before
   * timing out - note that this does not apply to synchronous parts of the execution graph
   * @return The list of subtasks contained within this command
   * @throws Exception Any exception occurring during the subtask creation process
   */
  protected abstract List<J> createSubtasks(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isAsynchronousExecution()
   */
  @Override
  protected boolean isAsynchronousExecution() {
    return false;
  }

  /**
   * Checks whether all subtasks have completed
   * @return Whether all subtasks have completed
   */
  private boolean isAllSubtasksCompleted() {

    for (J jobCommand : jobCommands) {
      if (!jobCommand.getJobCompletionStatus().isCompleted()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check whether at least one subtask has already completed unsuccessfully
   * @return Whether at least one subtask has already completed unsuccessfully
   */
  private boolean isAtLeastOneSubtaskCompletedUnsuccessfully() {

    for (J jobCommand : jobCommands) {
      if (jobCommand.getJobCompletionStatus().isCompletedWithError()) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected Future<JobSuccess> doExecute(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {

    SubtaskCoordinatorBehaviour behaviour = createSubtaskCoordinatorBehaviour();

    // Work out whether this is a first time execution or a retry, and initialise
    // job commands as appropriate

    boolean isCoordinatorRetry = false;
    if (jobCommands == null) {
      if (isCompletionStatusLoggingEnabled(executionContext)) {
        logger.trace(getLogMessage(executionContext, JobCommandEvent.JOB_INITIALISATION,
            "Breaking down job into subtasks"));
      }
      jobCommands = createSubtasks(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
    } else {
      updateSubtasksBeforeRetry(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
      isCoordinatorRetry = true;
    }

    // Setup some flags
    boolean previousExecutionResultedInAtLeastOneSubtaskError = false;
    if (isCoordinatorRetry) {
      previousExecutionResultedInAtLeastOneSubtaskError =
          isAtLeastOneSubtaskCompletedUnsuccessfully();
    }

    // Work out whether all subtasks have completed already.

    // If this is the first time of execution we know that all subtasks are not completed
    // If its a retry, check the completion status of all subtasks
    boolean allSubtasksCompleted = isCoordinatorRetry ? isAllSubtasksCompleted() : false;

    // Setup subtask status flags, and loop through subtasks, executing as needed
    boolean previousUncompletedSynchronousSubtask = false;
    boolean atLeastOneKnownSubtaskError = false;

    // Loop through the subtasks, executing-or-retrying as required
    for (J jobCommand : jobCommands) {

      // Check whether we need to skip the execution of all subsequent subtasks
      if (!isSkipSubsequentSubtaskExecution(behaviour, atLeastOneKnownSubtaskError)) {

        // If we are not skipping all subsequent subtasks, check whether we 
        // execute-or-retry this specific subtask
        if (isExecuteOrRetrySubtask(behaviour, jobCommand, allSubtasksCompleted,
            previousUncompletedSynchronousSubtask, isCoordinatorRetry,
            previousExecutionResultedInAtLeastOneSubtaskError)) {

          // We should execute-or-retry this subtask - make the call, and in the event
          // of an exception, decide how to proceed
          try {
            jobCommand.executeOrRetry(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
          } catch (Exception e) {
            // If a subtask throws an exception and hasn't yet completed, mark it as failed
            if (!jobCommand.getJobCompletionStatus().isCompleted()) {
              jobCommand.setUnsuccessfulCompletionUnixTime(UnixTime.now(), e);
            }
            // Set a flag, so we can decide whether to proceed further
            atLeastOneKnownSubtaskError = true;
          }
        }
        // Set a flag, so we know whether there were any previously uncompleted synchronous
        // subtasks
        if (!previousUncompletedSynchronousSubtask) {
          previousUncompletedSynchronousSubtask =
              !jobCommand.isAsynchronousExecution() && !jobCommand.getJobCompletionStatus()
                  .isCompleted();
        }
      }
    }

    return new FutureJobCommandSuccess<C>(this, executionContext,
        maxAsyncExecutionCompletionWaitTimeoutSecs);
  }

  /**
   * Decides whether to execute-or-retry a specific subtask, dependent on the
   * desired coordinator behaviour, and the status of various flags 
   *
   * @param desiredCoordinatorBehaviour The desired coordinator behaviour
   * @param subtask The specific subtask 
   * @param allSubtasksCompleted
   * @param previousUncompletedSynchronousSubtask
   * @param isCoordinatorRetry
   * @param previousExecutionResultedInAtLeastOneSubtaskError
   * @return Whether to execute-or-retry this subtask
   */
  private boolean isExecuteOrRetrySubtask(SubtaskCoordinatorBehaviour desiredCoordinatorBehaviour,
      J subtask, boolean allSubtasksCompleted, boolean previousUncompletedSynchronousSubtask,
      boolean isCoordinatorRetry, boolean previousExecutionResultedInAtLeastOneSubtaskError) {

    IfRetriedBeforeCompletion ifRetriedBeforeCompletionBehaviour =
        createSubtaskCoordinatorBehaviour().getIfRetriedBeforeCompletionBehaviour();

    boolean retryAllSubtasks = allSubtasksCompleted || ifRetriedBeforeCompletionBehaviour
        .equals(IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    JobStatus subtaskCompletionStatus = subtask.determineJobStatus();

    if (isCoordinatorRetry && previousExecutionResultedInAtLeastOneSubtaskError) {

      if (desiredCoordinatorBehaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError()
          && desiredCoordinatorBehaviour.getInterruptionStrategy().interruptOnPreCompletionWarning()
          && desiredCoordinatorBehaviour.getInterruptionStrategy()
          .completeWithErrorOnInterruption()) {
        return subtaskCompletionStatus.isCompleted();
      }
    }

    boolean firstCoordinatorExecution = !isCoordinatorRetry;
    boolean subtaskExecutionNotYetAttempted = subtask.getExecutionTimeUnix() == null;

    // Execute the subtask if:
    // it's the first coordinator execution, or a subtask re-execution, 
    // or we are instructed to retry
    // all subtasks, or if the desired coordinator behaviour permits the subtask
    // to be retried before completion
    if (firstCoordinatorExecution || subtaskExecutionNotYetAttempted || retryAllSubtasks
        || ifRetriedBeforeCompletionBehaviour
        .isSubtaskExecutionPermitted(subtaskCompletionStatus)) {

      // Don't execute if the subtask is synchronous and we have any previously uncompleted
      // subtasks, but execute otherwise
      if (subtask.isAsynchronousExecution() || !previousUncompletedSynchronousSubtask) {
        return true;
      }
    }
    return false;
  }

  /**
   * Decides whether to skip subsequent subtask execution given the desired coordinator behaviour
   * and flag indicating whether we have already had a subtask fail
   * @param behaviour
   * @param atLeastOneKnownSubtaskError
   *
   * @return Whether to skip subsequent subtask execution given the desired coordinator behaviour
   * and flag indicating whether we have already had a subtask fail
   */
  private boolean isSkipSubsequentSubtaskExecution(SubtaskCoordinatorBehaviour behaviour,
      boolean atLeastOneKnownSubtaskError) {

    RaiseWarning onSubtaskErrorBehaviour = behaviour.getRaiseWarningBehaviour();

    JobCommandInterruptionStrategy interruptionStrategy = behaviour.getInterruptionStrategy();

    boolean skipSubsequentSubtaskExecution =
        atLeastOneKnownSubtaskError && onSubtaskErrorBehaviour.raiseWarningOnSubtaskError()
            && (interruptionStrategy.interruptOnPreCompletionWarning());

    return skipSubsequentSubtaskExecution;
  }

  /**
   * Stats for the sub-tasks
   * @return The subtask stats
   */
  public SubtaskCoordinatorStats getSubtaskStats() {

    try {

      if (jobCommands == null) {
        return null;
      } else {
        int uncompletedSubtaskCount = 0;
        int completedWithErrorSubtaskCount = 0;
        int completedWithoutErrorSubtaskCount = 0;
        for (JobCommand<C> subtask : jobCommands) {
          JobCompletionStatus completionStatus = subtask.getJobCompletionStatus();
          if (completionStatus.isCompleted()) {

            if (completionStatus.isCompletedWithError()) {
              completedWithErrorSubtaskCount++;
            } else {
              completedWithoutErrorSubtaskCount++;
            }
          } else {
            uncompletedSubtaskCount++;
          }
        }

        return new SubtaskCoordinatorStats(uncompletedSubtaskCount,
            completedWithoutErrorSubtaskCount, completedWithErrorSubtaskCount);
      }
    } catch (Exception e) {
      logger.warn("Unable to obtain subtask coordinator stats", e);
      return null;
    }
  }

  /**
   * Resets the status of the delegate JobCommands
   */
  public void resetDelegatedStatus() {
    if (jobCommands != null) {
      for (JobCommand<C> subtask : jobCommands) {
        subtask.resetCompletionStatus();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#createInterruptStrategy()
   */
  @Override
  public final JobCommandInterruptionStrategy createInterruptStrategy() {
    return this.createSubtaskCoordinatorBehaviour().getInterruptionStrategy();
  }

  private JobStatus getInterruptedStatusIfInterruptOnPreCompletionWarning(
      SubtaskCoordinatorBehaviour subtaskCoordinatorBehaviour, JobStatus subtaskCompletionStatus) {
    if (subtaskCoordinatorBehaviour.getInterruptionStrategy().completeWithErrorOnInterruption()
        && subtaskCoordinatorBehaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError()) {
      return new JobFailedStatus(new RuntimeException(
          "At least one subtask completed with error:" + subtaskCompletionStatus.getException()
              .getMessage()));
    } else {

      // This will return a pre-completion warning
      if (subtaskCoordinatorBehaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError()) {
        return new JobWarningStatus(new RuntimeException(
            "At least one subtask completed with error:" + subtaskCompletionStatus.getException()
                .getMessage()));
      } else {
        return null;
      }
    }
  }

  /**
   * If we have configured the coordinator so we don't interrupt on pre-completion warning,
   * this method determines whether we can determine an "early status" ( without having
   * to loop through all subtasks), given the desired coordinator behaviour and the 
   * a subtask completion status
   *
   * @param subtaskCoordinatorBehaviour The desired coordinator behaviour
   * @param subtaskCompletionStatus The completion status of a specific subtask
   * @return An early status for the coordinator if it's possible to determine, or
   * null if its not possible to determine a status yet
   */
  private JobStatus getEarlyCoordinatorStatusIfNotInterruptOnPreCompletionWarning(
      SubtaskCoordinatorBehaviour subtaskCoordinatorBehaviour, JobStatus subtaskCompletionStatus) {

    if (subtaskCoordinatorBehaviour.getInterruptionStrategy().interruptOnPreCompletionWarning()) {
      throw new IllegalStateException("We must not have configured the coordinator to "
          + " interrupt on pre completion warning to be able to use this method");
    }

    if (!subtaskCompletionStatus.isCompleted()) {
      return new JobUnknownStatus();
    } else {
      return null;
    }

  }

  /**
   * Attempts to determine an "early status" for the coordinator if possible, given the
   * desired coordinator behaviour and an individual subtask completion status
   *
   * @param subtaskCoordinatorBehaviour The desired coordinator behaviour
   * @param subtaskCompletionStatus The completion status of a subtask
   * @return The coordinator status if it was possible to determine it "early" given
   * a subtask completion status, or null if it wasn't possible to determine the status
   * early
   */
  private JobStatus getEarlyCoordinatorStatus(
      SubtaskCoordinatorBehaviour subtaskCoordinatorBehaviour, JobStatus subtaskCompletionStatus) {

    JobStatus earlyStatus = null;

    boolean interruptOnPreCompletionWarning =
        subtaskCoordinatorBehaviour.getInterruptionStrategy().interruptOnPreCompletionWarning();

    if (subtaskCompletionStatus.isCompleted()) {
      // The subtask has completed
      if (subtaskCompletionStatus.isCompletedWithError()) {
        if (interruptOnPreCompletionWarning) {
          earlyStatus =
              getInterruptedStatusIfInterruptOnPreCompletionWarning(subtaskCoordinatorBehaviour,
                  subtaskCompletionStatus);
        }
      }
    } else {
      // The subtask has not completed yet
      if (!interruptOnPreCompletionWarning) {
        earlyStatus = getEarlyCoordinatorStatusIfNotInterruptOnPreCompletionWarning(
            subtaskCoordinatorBehaviour, subtaskCompletionStatus);
      }

    }
    return earlyStatus;
  }

  @Override
  protected JobStatus getDelegatedStatus() {

    if (jobCommands == null) {
      return new JobUnknownStatus();
    }

    SubtaskCoordinatorBehaviour subtaskCoordinatorBehaviour = createSubtaskCoordinatorBehaviour();

    // Setup initial status flags
    boolean atLeastOneSubtaskCompletedWithError = false;
    boolean allSubtasksCompleted = true;

    // Loop through the subtasks, setting status flags, and returning a status early if
    // it's possible, leaving the loop if so.
    for (JobCommand<C> subtask : jobCommands) {

      JobStatus subtaskCompletionStatus = subtask.determineJobStatus();

      JobStatus earlyStatus =
          getEarlyCoordinatorStatus(subtaskCoordinatorBehaviour, subtaskCompletionStatus);

      if (earlyStatus != null) {
        return earlyStatus;
      }

      if (subtaskCompletionStatus.isCompletedWithError()) {
        atLeastOneSubtaskCompletedWithError = true;
      }
      if (!subtaskCompletionStatus.isCompleted()) {
        allSubtasksCompleted = false;
      }
    }

    // Based on the flags set up when looping through the subtasks, determine the coordinator
    // status

    if (allSubtasksCompleted) {
      if (atLeastOneSubtaskCompletedWithError && subtaskCoordinatorBehaviour
          .getRaiseWarningBehaviour().raiseWarningOnSubtaskError()) {
        return new JobFailedStatus(
            new RuntimeException("At least one subtask completed with error"));
      } else {
        return new JobSuccess();

      }
    } else {

      if (atLeastOneSubtaskCompletedWithError && subtaskCoordinatorBehaviour
          .getRaiseWarningBehaviour().raiseWarningOnSubtaskError()) {
        return new JobWarningStatus(
            new RuntimeException("At least one subtask completed with error"));
      } else {
        return new JobUnknownStatus();
      }
    }
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.JobCommand#getProgressReport()
   */
  @Override
  public JobProgressReport getProgressReport() {
    JobProgressReport progressReport = getSubtaskStats();
    if (progressReport == null) {
      progressReport = new ProgressStatsUnavailable();
    }
    return progressReport;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getName() + ":" + getJobType();
  }

  /**
   * Gets the logger
   * @return a logger for the specific subclass of SubtaskCoordinatorJobCommand
   */
  protected abstract Logger getLogger();

}
