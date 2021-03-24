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

package com.echobox.jobqueue.status;

/**
 * A JobStatus which indicates that a job has been completed - and the status is either
 * COMPLETED_WITH_ERROR or COMPLETED_WITHOUT_ERROR.
 *
 * @author Michael Lavelle
 */
public abstract class JobCompletedStatus extends JobCompletionStatus {

  /**
   * Instantiates a new Job completed status.
   *
   * @param statusType The type of this status
   * @param exception The exception associated with this status if the status is
   *                  COMPLETED_WITH_ERROR - null should be provided for this parameter otherwise
   */
  protected JobCompletedStatus(JobStatusType statusType, Exception exception) {
    super(statusType, exception);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.status.JobStatus#isCompleted()
   */
  /**
   * Will always return true for this status
   */
  @Override
  public final boolean isCompleted() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.status.JobStatus#isCompletedWithoutError()
   */
  @Override
  public final boolean isCompletedWithoutError() {
    return !hasCompletionError();
  }
}
