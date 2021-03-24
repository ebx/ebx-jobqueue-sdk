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

package com.echobox.jobqueue.context;

import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.status.JobSuccess;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Execution context that a JobCommand is to run within
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public interface JobCommandExecutionContext<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable> {

  /**
   * JobCommandExecutionContext implementations should provide a method by which Callables
   * can be submit for asynchronous execution, returning a Future for JobSuccess  
   * 
   * This functionality is needed to support executing job commands locally (ie. not via a queue)
   * but asynchronously.
   * 
   * Implementations may choose to use thread pool executors to achieve this functionality or
   * may use a different approach.
   * 
   * @param callable A worker which returns JobSuccess on successful completion
   * @return A handle to the future JobSuccess, or to exceptions thrown during execution
   */
  Future<JobSuccess> executeAsync(Callable<JobSuccess> callable);

  /**
   * Is logging explicitly enabled for the specified job type.
   *
   * @param jobType the job type
   * @return Whether logging has been explicitly enabled for this job type
   */
  boolean isLoggingEnabledForJobType(JobType<?> jobType);

  /**
   * Obtains a reference to the PersistentQueuedJobCommandQueue enabled for this context
   *
   * @return The JobCommandQueue configured for this JobCommandExecutionContext which allows Jobs
   * to be wrapped in QueuedJobCommand decorators and executed by being sent to a queue and
   * subsequently executed by consumers
   */
  PersistentQueuedJobCommandQueue<C, Q, I> getJobCommandQueue();

  /**
   * Sets logging as explicitly enabled for all job types.
   *
   * @param enableLogging Whether to enable logging for all JobTypes
   */
  void setEnableLoggingForAllJobTypes(boolean enableLogging);

  /**
   * Sets logging as explicity enabled for the specified job type
   *
   * @param jobType the job type
   */
  void enableLoggingForJobType(JobType<?> jobType);

  /**
   * Returns a flag to indicate whether either application or context shutdown is requested
   *
   * @return Whether either application or context shutdown is requested
   */
  boolean isShutdownRequested();

  /**
   * Returns a flag to indicate whether either application shutdown is requested
   *
   * @return Whether application shutdown is requested
   */
  boolean isApplicationShutdownRequested();

}
