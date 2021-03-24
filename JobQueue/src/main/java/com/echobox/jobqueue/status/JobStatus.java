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
 * Encapsulates the runtime status of a Job ( which may or may not be a JobCompletionStatus )
 *
 * @author Michael Lavelle
 */
public abstract class JobStatus {

  private JobStatusType statusType;
  private Exception exception;

  /**
   * Gets status type.
   *
   * @return statusType status type
   */
  public JobStatusType getStatusType() {
    return statusType;
  }

  /**
   * Instantiates a new Job status.
   *
   * @param statusType The type of this status
   * @param exception Any exception associated with this status - either an exception which has
   *                  led to failure ( if COMPLETED_WITH_ERROR ),  or warning ( if
   *                  UNCOMPLETED_WITH_WARNING )
   */
  protected JobStatus(JobStatusType statusType, Exception exception) {
    this.exception = exception;
    this.statusType = statusType;
  }

  /**
   * Is completed without error boolean.
   *
   * @return Whether this job has completed without error
   */
  public abstract boolean isCompletedWithoutError();

  /**
   * Is completed with error boolean.
   *
   * @return Whether this job has completed with error
   */
  public abstract boolean isCompletedWithError();

  /**
   * Is completed boolean.
   *
   * @return Whether this job is completed ( either with or without error )
   */
  public abstract boolean isCompleted();

  /**
   * Gets exception.
   *
   * @return Any exception associated with this status - either an exception which has led to
   * failure ( completed with error ),  or warning ( uncompleted with warning )
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Has completion error boolean.
   *
   * @return Whether this job is completed with error ( failed )
   */
  public abstract boolean hasCompletionError();

  /**
   * Has pre completion warning boolean.
   *
   * @return Whether this job is uncompleted, but with a warning
   */
  public abstract boolean hasPreCompletionWarning();

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "JobStatus [statusType=" + statusType + ", exception=" + exception + "]";
  }
}
