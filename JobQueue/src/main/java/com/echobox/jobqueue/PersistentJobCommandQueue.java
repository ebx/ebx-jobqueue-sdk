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

import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.context.JobCommandExecutionContext;

import java.io.Serializable;

/**
 * 
 * A PersistentJobCommandQueue is type of JobQueue that allows JobCommands to be queued and
 * persisted so that their runtime status can be stored.
 * 
 * Implementations of this interface queue JobCommands which execute within a specified type of
 * JobCommandExecutionContext ( of type  C ).
 * 
 * Methods are provided to support queueing and persistence of the JobCommands along with
 * methods for managing their consumption/execution status.
 *
 * Polling or peeking at elements of the queue yields PersistentJobCommandOnQueue instances
 * 
 * @param <C> The type of JobCommandExecutionContext the JobCommands will execute within
 * @param <Q> The type of queue-type we are using to distinguish between different types of queue
 * @param <I> The type of unique identifier for each Job stored on the queue
 * 
 * @author Michael Lavelle
 */
public interface PersistentJobCommandQueue<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable>
    extends JobQueue<Q, I, JobCommand<C>, PersistentJobCommandOnQueue<C, Q, I>>,
    JobCommandOnQueueRepository<C, Q, I> {
  
  /**
   * Reset the JobCommand with specified id on the queue, so that it is eligible for 
   * re-consumption
   *
   * @param queuedJobId The queueType and jobId which identify the job on the queue
   * @return true if the job exists and was reset, false otherwise
   */
  boolean resetJob(QueuedJobId<Q, I> queuedJobId);
}
