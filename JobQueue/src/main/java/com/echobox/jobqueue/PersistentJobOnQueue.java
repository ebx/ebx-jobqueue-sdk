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
 * A PersistentJobOnQueue is a JobOnQueue that has been saved to a repository, 
 * together with details of consumption from the queue ( such as the consumption time, 
 * the consumerId and any heartbeat time last set by a consumer).
 * 
 * Storing these details alongside the JobOnQueue details allow determinations to be made
 * about whether a JobOnQueue is currently consumed ( not available for dequeuing ), unconsumed
 * ( available for dequeuing ), and whether any consumers have recently updating the job's 
 * heartbeat.
 * 
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of the job id - a unique identifier for each job on the queue
 * @param <J> The type of Job which is being queued
 * 
 * @author Michael Lavelle
 */
public interface PersistentJobOnQueue<Q extends Serializable, I extends Serializable, 
    J extends Job> extends JobOnQueue<Q, I, J> {

  /**
   * Gets the job
   * @return The job that was sent to the JobQueue.
   */
  J getJob();
  
  /**
   * Gets the queued job id
   * @return The id of the job on the queue
   */
  QueuedJobId<Q, I> getId();

  /**
   * Obtains the unix time of the last heartbeat time for this job as set by a consumer, or
   * null if no last heartbeat time
   *
   * @return The last heartbeat time
   */
  Long getLastHeartbeatTimeUnix();

  /**
   * Obtains the unix time of the consumption of this job, or null if the job has not been
   * consumed
   *
   * @return The consumption time unix
   */
  Long getConsumptionTimeUnix();

  /**
   * Obtains the id of any consumer which is consuming the job, or null if the job 
   * is not being consumed
   *
   * @return The consumer id
   */
  String getConsumerId();
  
  /**
   * Sets the consumerId
   * @param consumerId The consumerId
   */
  void setConsumerId(String consumerId);
  
  /**
   * Sets the consumption unix time
   * @param consumptionTimeUnix The consumption unix time
   */
  void setConsumptionTimeUnix(Long consumptionTimeUnix);
 
  /**
   * Sets the last heartbeat unix time
   * @param lastHeartbeatTimeUnix The last heartbeat unix time
   */
  void setLastHeartbeatTimeUnix(Long lastHeartbeatTimeUnix);
}
