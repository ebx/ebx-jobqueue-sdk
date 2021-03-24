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

import java.io.Serializable;

/**
 * A JobOnQueue encapsulates a Job that has been added to a JobQueue, and therefore wraps a
 * Job with an associated QueuedJobId (to indicate queueType and unique jobId)
 *
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of the job id - a unique identifier for each job on the queue
 * @param <J> The type of Job which is being queued
 * 
 * @author Michael Lavelle
 */
public interface JobOnQueue<Q extends Serializable, I extends Serializable, J extends Job> {

  /**
   * Returns a wrapper around the queue type and job id for this Job.
   *
   * @return The id
   */
  QueuedJobId<Q, I> getId();

  /**
   * Returns the Job as added to the Queue
   *
   * @return The job
   */
  J getJob();
}
