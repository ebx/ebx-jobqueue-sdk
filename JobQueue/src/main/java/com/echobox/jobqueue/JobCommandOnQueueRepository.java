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

import com.echobox.jobqueue.context.JobCommandExecutionContext;

import java.io.Serializable;
import java.util.List;

/**
 * A repository interface for PersistentJobCommandOnQueue instances - wrappers around
 * JobCommands which have been added to a JobQueue and are to be persisted along with details
 * of consumption.
 * 
 * In addition to the CRUD methods for PersistentJobCommandOnQueue instances themselves, this
 * interface also defines methods to update the status of completion status and execution-time
 * heartbeats of any existing PersistentJobCommandOnQueue instances in the repository
 * 
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public interface JobCommandOnQueueRepository
    <C extends JobCommandExecutionContext<C, Q, I>, Q extends Serializable, 
    I extends Serializable> {

  /**
   * Saves/Updates a persistentJobCommandOnQueue instance
   * 
   * Requires that at least the queueType is specified on the queuedJobId of the
   * persistentJobCommandOnQueue to be saved
   * 
   * If the persistentJobCommandOnQueue we wish to save has already been persisted ( ie. this
   * is an update), the id associated with the persistentJobCommandOnQueue will be known to 
   * be valid and implementing classes should always support this use-case, as 
   * 
   * If the persistentJobCommandOnQueue has not been saved before, the jobId specified
   * by the queuedJobId of the persistentJobCommandOnQueue may be null and should be assigned
   * by the repository.
   * 
   * It is up to implementing classes whether to support the situation where an jobId is provided
   * but the jobId is not one the repository has seen before.  For example, if a jobId is provided
   * which is not in the valid format, implementations are free to throw exceptions to prevent
   * a save.
   * 
   * @param persistentJobCommandOnQueue Details of the JobCommand as queued, along with
   * the queueType, jobId, and consumption details
   * @return A wrapper around the queue type and jobId of the saved job
   */
  QueuedJobId<Q, I> saveJob(PersistentJobCommandOnQueue<C, Q, I> persistentJobCommandOnQueue);
  
  /**
   * Returns the PersistentJobCommandOnQueue with this QueuedJobId
   *
   * @param queuedJobId A wrapper around the queueType and jobId which identify the job on the 
   * queue
   * @return The PersistentJobCommandOnQueue on the queue with this queuedJobId, 
   * or null if no such job exists
   */
  PersistentJobCommandOnQueue<C, Q, I> getJob(QueuedJobId<Q, I> queuedJobId);
  
  /**
   * Deletes the PersistentJobCommandOnQueue with this queuedJobId
   * 
   * @param queuedJobId A wrapper around the queueType and jobId which identify the job on the 
   * queue
   * @return Whether the job was deleted
   */
  boolean deleteJob(QueuedJobId<Q, I> queuedJobId);
  
  /**
   * Delete all PersistentJobCommandOnQueue instances from this repository whose target 
   * queueType is set to the specified queueType .
   *
   * @param queueType The queue type we wish to purge
   * @return Whether the queue is now empty
   */
  boolean deleteAllJobs(Q queueType);
  
  /**
   * Returns the list of PersistentJobCommandOnQueue within this repository whose target 
   * queueType is set to the specified queueType and which have been consumed, but not yet 
   * removed from the repository.
   * 
   * @param queueType The queue type of the queue we wish to access.
   * @return A list of details of all consumed jobs
   */
  List<PersistentJobCommandOnQueue<C, Q, I>> getAllConsumedJobs(
      Q queueType);
  
  /**
   * Delete all unconsumed PersistentJobCommandOnQueue instances from this repository 
   * whose target queueType is set to the specified queue type
   *
   * @param queueType the queue type
   * @return Whether the delete was successful
   */
  boolean deleteAllUnconsumedJobs(Q queueType);
  
  /**
   * Enables a producer to reserve all unconsumed PersistentJobCommandOnQueue within this 
   * repository whose target queueType is set to the specified queue type.   
   * 
   * Reserving these jobs means that they are no longer available for consumption by consumers.
   * 
   * We provide a producerId which is set on the reserved jobs in place of a consumerId - setting 
   * the consumerId as non-null on these jobs prevents these jobs from being consumed
   * 
   * This should be an atomic operation which can be called before a delete to ensure that 
   * consumption is halted.  If it is not possible to amend the allocatedConsumerId on all
   * unconsumed jobs atomically, implementations could instead implement an alternative mechansism
   * of halting consumption by non-producer clients.
   *
   * @param queueType the queue type
   * @param producerId the producer id
   * @return Whether the reserve was successful
   */
  boolean reserveAllUnconsumedJobs(Q queueType, String producerId);
  
  /**
   * Update the PersistentJobCommandOnQueue with this queuedJobId to indicate successful
   * completion by the consumer with the specified consumerId
   * 
   * @param queuedJobId The queued job id
   * @param consumerId The consumer id
   * @param completedUnixTime The completed unix time
   * @return Whether the job was marked as successfully completed
   */
  boolean setJobSuccessfulCompletion(QueuedJobId<Q, I> queuedJobId, String consumerId,
      long completedUnixTime);

  /**
   * Update the PersistentJobCommandOnQueue with this queuedJobId to indicate unsuccessful
   * completion by the consumer with the specified consumerId
   * 
   * @param queuedJobId The queued job id
   * @param exception The exception which caused the unsuccessful completion
   * @param consumerId The consumerId of the consumer executing the job
   * @param completedUnixTime The completed unix time
   * @return Whether the job was marked as unsuccessfully completed
   */
  boolean setJobUnsuccessfulCompletion(QueuedJobId<Q, I> queuedJobId, Exception exception,
      String consumerId, long completedUnixTime);

  /**
   * Update the PersistentJobCommandOnQueue with this queuedJobId with the heartbeat time
   * as specified by a consumer
   * 
   * @param queuedJobId The queued job id
   * @param consumerId The consumerId of the consumer updating the heartbeat time
   * @param heartbeatUnixTime The heartbeat unix time
   * @return Whether the heartbeat time was updated
   */
  boolean updateHeartbeatTime(QueuedJobId<Q, I> queuedJobId, String consumerId,
      long heartbeatUnixTime);
  
  /**
   * Obtain the count of PersistentJobCommandOnQueue instances within this repository whose 
   * target queueType is set to the specified queue type.
   * 
   * @param queueType The type of queue to query for a count
   * @return The count of jobs in this repository whose target queueType is specified by this 
   * queueType
   */
  long getJobCount(Q queueType);
}
