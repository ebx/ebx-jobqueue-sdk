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

/**
 * JobCommands can always be interrupted by TimeoutExceptions or InterruptExceptions, but a
 * JobCommandInterruptionStrategy indicates other circumstances in which a JobCommand can
 * be interrupted and defines the resulting behaviour - for example what to do when an early
 * warning is raised because of a subtask error.
 *
 * @author Michael Lavelle
 */
public class JobCommandInterruptionStrategy {

  /**
   * No specified interruption strategy
   */
  public static final JobCommandInterruptionStrategy NONE =
      new JobCommandInterruptionStrategy(false, false);

  /**
   * Interrupt execution, but do not complete the job on warning
   */
  public static final JobCommandInterruptionStrategy INTERRUPT_BUT_DO_NOT_COMPLETE_ON_WARNING =
      new JobCommandInterruptionStrategy(true, false);

  /**
   * Interrupt execution, and complete with error on warning
   */
  public static final JobCommandInterruptionStrategy INTERRUPT_AND_COMPLETE_WITH_ERROR_ON_WARNING =
      new JobCommandInterruptionStrategy(true, true);

  private boolean interruptOnPreCompletionWarning;
  private boolean completeWithErrorOnInterruption;

  /**
   * Instantiates a new Job command interruption strategy.
   *
   * @param interruptOnPreCompletionWarning the interrupt on pre completion warning
   * @param completeWithErrorOnInterruption the complete with error on interruption
   */
  public JobCommandInterruptionStrategy(boolean interruptOnPreCompletionWarning,
      boolean completeWithErrorOnInterruption) {
    this.interruptOnPreCompletionWarning = interruptOnPreCompletionWarning;
    this.completeWithErrorOnInterruption = completeWithErrorOnInterruption;
  }

  /**
   * Interrupt on pre completion warning boolean.
   *
   * @return Whether to interrupt the JobCommand when a pre-completion warning is raised, such as
   * when a subtask fails.
   */
  public boolean interruptOnPreCompletionWarning() {
    return interruptOnPreCompletionWarning;
  }

  /**
   * Complete with error on interruption boolean.
   *
   * @return Whether the JobCommand's status should be set to COMPLETED_WITH_ERROR whenever the
   * JobCommand is interrupted.
   */
  public boolean completeWithErrorOnInterruption() {
    return completeWithErrorOnInterruption;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    JobCommandInterruptionStrategy other = (JobCommandInterruptionStrategy) obj;
    if (completeWithErrorOnInterruption != other.completeWithErrorOnInterruption) {
      return false;
    }
    if (interruptOnPreCompletionWarning != other.interruptOnPreCompletionWarning) {
      return false;
    }
    return true;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (completeWithErrorOnInterruption ? 1231 : 1237);
    result = prime * result + (interruptOnPreCompletionWarning ? 1231 : 1237);
    return result;
  }
}
