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

import org.javatuples.Pair;

import java.io.Serializable;

/**
 * A JobQueue is responsible for queueing Jobs
 *
 * A JobQueue enqueues Jobs of type E and stores/dequeues JobOnQueue instances of type D.
 *
 * When stored on the queue, the objects are stored/dequeued by specifying a queue-type
 * ( of type Q ), and a jobId ( of type I ), which together form a {@code QueuedJobId<Q,I>}
 *
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * @param <E> The type of the Job we enqueue
 * @param <D> The type of the JobOnQueue we dequeue
 * 
 * @author Michael Lavelle
 */
public interface JobQueue<Q extends Serializable, I extends Serializable, 
    E extends Job, D extends JobOnQueue<Q, I, ?>> {

  /**
   * Add this job to the relevant queue as specified by the queueType, and if necessary persist
   * the job to a persistence store if this implementation is a persistent queue
   * 
   * The id assigned to this job is determined by this job queue implementation 
   *
   * @param job The Job to enqueue
   * @param queueType The type of queue we want to add the job to
   * @return A wrapper around the queueType and jobId which identify the job on the queue
   */
  QueuedJobId<Q, I> addJobToQueue(E job, Q queueType);
 
  /**
   * Add this job to the relevant queue as specified by the queueType of the queuedJobId, 
   * and with id specified by the jobId of the queuedJobId.  If necessary persist
   * the job to a persistence store if this implementation is a persistent queue.
   * 
   * This method is primarily used for use-cases where we wish to re-add a job to a queue and
   * that job already is associated with an id.  In this case we know that the id is a valid
   * id for the queue, and implementing classes should support this use-case.
   * 
   * NB:  This method may not support adding a job to a queue with an arbitrary id that hasn't
   * already been assigned by the queue previously - this is up to individual implementations
   * to determine whether to support this use case. 
   *
   * @param job The Job to enqueue
   * @param queuedJobId The queued Job id
   * @return A wrapper around the queueType and jobId which identify the job on the queue
   */
  QueuedJobId<Q, I> addJobToQueueWithId(E job, QueuedJobId<Q, I> queuedJobId);

  /**
   * Obtain the next JobOnQueue available for consumption by a consumer with the specified 
   * consumerId.
   * 
   * The next available JobOnQueue is returned (along with its queuedJobId) - or null is 
   * returned if no next job is available for this consumer from the specified queueType
   * 
   * @param consumerId The id of the consumer which is consuming the JobOnQueue
   * @param queueType The type of queue we will get the next JobOnQueue from
   * @return The next available dequeued JobOnQueue and its id from the queue, or null if 
   * no jobs available for this queueType and consumerId
   */
  Pair<QueuedJobId<Q, I>, D> getNextJob(String consumerId, Q queueType);
  
  /**
   * Return the number of jobs on the queue specified by queueType. Depending on the implementation
   * this count could also include the jobs that have been dequeued from the queue and are still
   * considered to be active.
   * 
   * @param queueType The type of queue to query for the count
   * @return The count of Jobs on the queue of this queueType - may include the count of dequeued
   * but still active jobs, dependent on the implementation
   */
  long getJobCount(Q queueType);
}
