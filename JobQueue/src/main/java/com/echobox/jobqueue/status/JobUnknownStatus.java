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
 * A JobStatus which indicates that a Job's completion status is UNKNOWN
 *
 * @author Michael Lavelle
 */
public class JobUnknownStatus extends JobCompletionStatus {

  /**
   * Default constructor
   */
  public JobUnknownStatus() {
    super(JobStatusType.UNKNOWN, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.status.JobStatus#isCompleted()
   */

  /**
   * Will always return false for this status
   */
  @Override
  public final boolean isCompleted() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.status.JobStatus#isCompletedWithoutError()
   */

  /**
   * Will always return false for this status
   */
  @Override
  public final boolean isCompletedWithoutError() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.status.JobStatus#hasCompletionError()
   */

  /**
   * Will always return false for this status
   */
  @Override
  public final boolean hasCompletionError() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.status.JobStatus#hasPreCompletionWarning()
   */

  /**
   * Will always return false for this status
   */
  @Override
  public final boolean hasPreCompletionWarning() {
    return false;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.status.JobStatus#isCompletedWithError()
   */

  /**
   * Will always return false for this status
   */
  @Override
  public final boolean isCompletedWithError() {
    return false;
  }
}
