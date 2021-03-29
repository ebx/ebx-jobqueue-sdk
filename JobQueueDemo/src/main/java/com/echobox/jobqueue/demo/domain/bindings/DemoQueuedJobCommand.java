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

package com.echobox.jobqueue.demo.domain.bindings;

import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.commands.QueuedJobCommand;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import org.bson.types.ObjectId;

/**
 * Demo queued job command
 * @author Michael Lavelle
 * 
 * @param <J> The type of JobCommand we are queueing
 *
 */
public class DemoQueuedJobCommand<J extends JobCommand<DemoCommandExecutionContext>> 
    extends QueuedJobCommand<DemoCommandExecutionContext, DemoQueueType, ObjectId, J> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * Initialise DemoQueuedJobCommand
   * @param jobQueue The persistent queued job command queue
   * @param queueType The queue type we are sending the job command to
   * @param queuedJob The job command we are queueing
   * @param jobCreationTimeUnix The creation time of this command
   */
  public DemoQueuedJobCommand(
      PersistentQueuedJobCommandQueue<DemoCommandExecutionContext, DemoQueueType, 
      ObjectId> jobQueue,
      DemoQueueType queueType, J queuedJob, long jobCreationTimeUnix) {
    super(jobQueue, queueType, queuedJob, jobCreationTimeUnix);
  }

  /**
   * Initialise DemoQueuedJobCommand
   * @param jobQueue The persistent queued job command queue
   * @param queuedJob The job command we are queueing
   * @param jobCreationTimeUnix The creation time of this command
   * @param queuedJobId The queued job id of the command on the queue
   */
  public DemoQueuedJobCommand(
      PersistentQueuedJobCommandQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
      jobQueue, J queuedJob, long jobCreationTimeUnix, QueuedJobId<DemoQueueType, ObjectId> 
      queuedJobId) {
    super(jobQueue, queuedJob, jobCreationTimeUnix, queuedJobId);
  }
}
