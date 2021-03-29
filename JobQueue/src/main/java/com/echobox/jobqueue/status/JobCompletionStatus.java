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
 * A JobStatus which indicates whether a Job's completion status is UNKNOWN, COMPLETED_WITH_ERROR
 * or COMPLETED_WITHOUT_ERROR.
 *
 * Until a job has been explicitly declared to be completed, this status will be UNKNOWN
 *
 * @author Michael Lavelle
 */
public abstract class JobCompletionStatus extends JobStatus {

  /**
   * Instantiates a new Job completion status.
   *
   * @param statusType The type of this JobCompletionStatus ( whether UNKNOWN,
   *                   COMPLETED_WITH_ERROR or COMPLETED_WITHOUT_ERROR )
   * @param exception Any exception which may have cause the job to become COMPLETED_WITH_ERROR -
   *                  null should be provided for other states
   */
  protected JobCompletionStatus(JobStatusType statusType, Exception exception) {
    super(statusType, exception);
  }
}
