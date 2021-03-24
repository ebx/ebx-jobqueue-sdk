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

package com.echobox.jobqueue.commands.behaviour;

import com.echobox.jobqueue.status.JobStatus;

/**
 * Behavioural strategy for SubtaskCoordinatorJobCommands
 *
 * @author Michael Lavelle
 */
public class SubtaskCoordinatorBehaviour {

  private RaiseWarning raiseWarningBehaviour;
  private InterruptWaitForCompletion interruptWaitForCompletionBehaviour;
  private IfWaitForCompletionIsInterrupted ifWaitForCompletionIsInterruptedBehaviour;
  private IfRetriedBeforeCompletion ifRetriedBeforeCompletionBehaviour;

  /**
   * When to raise a warning - for example on subtask error
   * @return When to raise a warning - for example on subtask error
   */
  public RaiseWarning getRaiseWarningBehaviour() {
    return raiseWarningBehaviour;
  }

  /**
   * Gets the interruption strategy
   * @return The circumstances in which the JobCommand will be interrupted ( over and above
   * expected interruption such as those caused by timeouts ), and the desired behaviour
   * should such interruptions occur.
   */
  public JobCommandInterruptionStrategy getInterruptionStrategy() {
    return new JobCommandInterruptionStrategy(
        interruptWaitForCompletionBehaviour.interruptOnPreCompletionWarning(),
        ifWaitForCompletionIsInterruptedBehaviour.completeWithErrorOnInterruption());
  }

  /**
   * Gets the Behaviour of this the SubtaskCoordinatorJobCommand
   * @return The Behaviour of this the SubtaskCoordinatorJobCommand if a client re-executes
   * ( "retries" ) the Job.  Please note that does not indicate whether the task will be retried on 
   * error, but only the behaviour if a client does retry the job
   */
  public IfRetriedBeforeCompletion getIfRetriedBeforeCompletionBehaviour() {
    return ifRetriedBeforeCompletionBehaviour;
  }

  /**
   * Instantiates a new Subtask coordinator behaviour.
   *
   * @param onSubtaskErrorBehaviour the on subtask error behaviour
   * @param interruptionStrategy the interruption strategy
   * @param interruptionBehaviour the interruption behaviour
   * @param onRetryBehaviour the on retry behaviour
   */
  public SubtaskCoordinatorBehaviour(RaiseWarning onSubtaskErrorBehaviour,
      InterruptWaitForCompletion interruptionStrategy,
      IfWaitForCompletionIsInterrupted interruptionBehaviour,

      IfRetriedBeforeCompletion onRetryBehaviour) {
    super();
    this.raiseWarningBehaviour = onSubtaskErrorBehaviour;
    this.interruptWaitForCompletionBehaviour = interruptionStrategy;
    this.ifRetriedBeforeCompletionBehaviour = onRetryBehaviour;
    this.ifWaitForCompletionIsInterruptedBehaviour = interruptionBehaviour;
  }

  /**
   * Defines the desired warning behaviour on a subtask error
   * @author Michael Lavelle
   */
  public enum RaiseWarning {

    /**
     * We want to ignore any subtask error
     */
    NEVER(false), 
    
    /**
     * We want to raise a warning for an subtask error
     */
    ON_SUBTASK_ERROR(true);

    private boolean raiseWarningOnSubtaskError;

    RaiseWarning(boolean raiseWarningOnSubtaskError) {
      this.raiseWarningOnSubtaskError = raiseWarningOnSubtaskError;
    }

    /**
     * Raise warning on subtask error boolean.
     *
     * @return the boolean
     */
    public boolean raiseWarningOnSubtaskError() {
      return raiseWarningOnSubtaskError;
    }
  }

  /**
   * If we have defined RaiseWarning.NEVER, the value of the InterruptWaitForCompletion enum has 
   * no effect on the behaviour - however if we have defined RaiseWarning.ON_SUBTASK_ERROR, 
   * this enum allows the desired behaviour on the warning to be defined.
   * @author Michael Lavelle
   */
  public enum InterruptWaitForCompletion {

    /**
     * We want to continue processing remaining subtasks
     */
    NEVER(false), 
    
    /**
     * Interrupt other subtasks that are waiting for completion
     */
    ON_FIRST_WARNING(true);
  
    /**
     * Interrupt on precompletion warning
     */
    boolean interruptOnPreCompletionWarning;
  
    /**
     * The constructor
     * @param interruptOnPreCompletionWarning the interruptOnPreCompletionWarning value
     */
    InterruptWaitForCompletion(boolean interruptOnPreCompletionWarning) {
      this.interruptOnPreCompletionWarning = interruptOnPreCompletionWarning;
    }

    /**
     * Gets the interruptOnPreCompletionWarning value
     * @return Whether to interrupt the JobCommand when a pre-completion warning is raised, such
     * as when a subtask fails.
     */
    public boolean interruptOnPreCompletionWarning() {
      return interruptOnPreCompletionWarning;
    }
  }

  /**
   * If we have specified that we never want to interrupt the wait for completion on subtask errors,
   * the value of the IfWaitForCompletionIsInterrupted has no effect (within the current
   * subtask coorinator execution). However if we have specified 
   * InterruptWaitForCompletion.ON_FIRST_WARNING, the IfWaitForCompletionIsInterrupted 
   * enum defines what should happen to the coordinator task.
   * @author Michael Lavelle
   */
  public enum IfWaitForCompletionIsInterrupted {

    /**
     * Leave the coordinator task as uncompleted. Leaving the parent coordinator task as 
     * uncompleted allows the potential for a retry attempt on the coordinator continuing where 
     * it left off.
     */
    DONT_COMPLETE(false), 
    
    /**
     * Mark the coordinator task as completed with error. Marking the parent coordinator task as 
     * completed with error will cause any coordinator retry attempts to retry all subtasks.
     */
    COMPLETE_WITH_ERROR(true);
  
    /**
     * Complete with error on interruption
     */
    boolean completeWithErrorOnInterruption;
  
    /**
     * The constructor
     * @param completeWithErrorOnInterruption the completeWithErrorOnInterruption value
     */
    IfWaitForCompletionIsInterrupted(boolean completeWithErrorOnInterruption) {
      this.completeWithErrorOnInterruption = completeWithErrorOnInterruption;
    }

    /**
     * Gets the completeWithErrorOnInterruption
     * @return Whether the JobCommand's status should be set to COMPLETED_WITH_ERROR whenever the
     * JobCommand is interrupted.
     */
    public boolean completeWithErrorOnInterruption() {
      return completeWithErrorOnInterruption;
    }
  }

  /**
   * Defines what we want to do if a subtask coordinator command is retried before it has been 
   * marked as completed (for example if a subtask has errored and 
   * IfWaitForCompletionIsInterrupted.DONT_COMPLETE has been specified, or in the event a producer 
   * wishes to trigger a retry in order to re-execute any substasks which have completed)
   * @author Michael Lavelle
   */
  public enum IfRetriedBeforeCompletion {

    /**
     * We want to simply continue to wait
     */
    JUST_CONTINUE_TO_WAIT,
    
    /**
     * Retry all subtasks
     */
    RETRY_ALL_SUBTASKS,
    
    /**
     * Retry all subtasks that have completed, either successfully or unsuccessfully 
     */
    RETRY_ALL_COMPLETED_SUBTASKS,
    
    /**
     * Retry only those subtasks which have failed
     */
    RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS;

    /**
     * Decides whether execution is permitted for a specific subtask when the 
     * coordinator job is retried before completion, given the status of the subtask
     *
     * @param subtaskStatus The status of a specific subtask
     * @return Whether this behaviour permits execution of a subtask with the provided
     * status when the coordinator job is retried before completion
     */
    public boolean isSubtaskExecutionPermitted(JobStatus subtaskStatus) {
      if (this.equals(JUST_CONTINUE_TO_WAIT)) {
        return false;
      } else if (this.equals(RETRY_ALL_SUBTASKS)) {
        return true;
      } else if (subtaskStatus.isCompleted()) {
        if (this.equals(RETRY_ALL_COMPLETED_SUBTASKS)) {
          return true;
        } else {
          return subtaskStatus.hasCompletionError();
        }
      } else {
        return false;
      }
    }

  }
}
