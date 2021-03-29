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

import com.echobox.jobqueue.commands.DequeuedJobCommand;
import com.echobox.jobqueue.commands.QueuedJobCommand;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.status.JobStatus;

import java.io.Serializable;

/**
 * A PersistentQueuedJobCommandQueue is a PersistentJobCommandQueue which enqueues 
 * QueuedJobCommand instances and dequeues DequeuedJobCommand instances.
 *
 * The enqueued/dequeued JobCommands execute within a specific JobCommandExecutionContext
 * of type C.
 *
 * These commands wrap another JobCommand instance ( of any type) that we wish
 * to execute in the same type of context, and provide additional life-cycle functionality -
 * eg. exception handling and status reporting
 *
 * When stored on the queue, the commands are stored/retrieved by specifying a queue-type
 * ( of type Q ), and an jobId ( of type I ), which together form a {@code QueuedJobId<Q,I>}
 *
 * The PersistentQueuedJobCommandQueue is responsible for handling explicit calls to update the 
 * completion status of each QueuedJobCommand on the queue, but also provides a mechanism to 
 * query the completion status more dynamically - returning a status that can take into account 
 * dynamic events such as no-recent heartbeat.
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public interface PersistentQueuedJobCommandQueue<C extends 
    JobCommandExecutionContext<C, Q, I>, Q extends Serializable, I extends Serializable> 
    extends JobQueue<Q, I, QueuedJobCommand<C, Q, I, ?>, 
    DequeuedJobCommand<C, Q, I>>, JobCommandOnQueueRepository<C, Q, I> {

  /**
   * Reset the QueuedJobCommand with specified id on the queue, so that it is eligible for 
   * re-consumption
   *
   * @param queuedJobId The queueType and jobId which identify the job on the queue
   * @return true if the job exists and was reset, false otherwise
   */
  boolean resetJob(QueuedJobId<Q, I> queuedJobId);
  
  /**
   * Checks the queue and determines the status of the QueuedJobCommand with this queuedJobId.
   *
   * This method may return the explicitly-set completion status of the job, but may also
   * take dynamic events into consideration ( such as no-recent-heartbeat) to return
   * a different status
   *
   * @param queuedJobId The queueType and jobId which identify the job on the queue
   * @return The dynamic status of this job command
   */
  JobStatus determineJobStatus(QueuedJobId<Q, I> queuedJobId);

}
