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

package com.echobox.jobqueue;

import com.echobox.jobqueue.status.JobCompletionStatus;
import com.echobox.jobqueue.status.JobStatus;

import java.io.Serializable;

/**
 * Jobs are instructions which can be added to a JobQueue.
 * 
 * Jobs are constructed with a specified JobType and a creation time.
 * 
 * They have a JobCompletionStatus which indicates whether the job has completed, and if so
 * whether the completion was successful or unsuccessful.
 * 
 * Jobs can also be asked to determine their runtime status at any time.
 *
 * The runtime status may be a JobCompletionStatus, or it may be an interim JobStatus such as a 
 * JobWarningStatus.
 *
 * @author Michael Lavelle
 */
public interface Job extends Serializable {

  /**
   * Gets job type.
   *
   * @return The type of job
   */
  JobType<?> getJobType();
  
  /**
   * Gets job creation time unix.
   *
   * @return The unix time when this job was created
   */
  long getJobCreationTimeUnix();
  
  /**
   * Gets job completion status.
   *
   * @return The explicitly set job completion status
   */
  JobCompletionStatus getJobCompletionStatus();

  /**
   * Determine job status job status.
   *
   * @return The determined job status at the request time
   */
  JobStatus determineJobStatus();
}
