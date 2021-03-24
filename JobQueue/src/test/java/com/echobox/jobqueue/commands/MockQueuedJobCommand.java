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

package com.echobox.jobqueue.commands;

import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;

import java.io.Serializable;

/**
 *  A mock extension for QueuedJobCommand used for unit testing 
 *  @author Alexandros
 */
public class MockQueuedJobCommand extends QueuedJobCommand<MockJobCommandExecutionContext, 
    Serializable, Serializable, MockWorkerCommand> {

  /**
   * The constructor for initializing the QueuedJobCommand
   * @param jobQueue A mocPersistentQueuedJobCommandQueue 
   * @param queueType A queueType any Serializable object 
   * @param queuedJob A queuedJob any Serializable object
   * @param jobCreationTimeUnix the creation time.
   */
  public MockQueuedJobCommand(
      PersistentQueuedJobCommandQueue
          <MockJobCommandExecutionContext, Serializable, Serializable> jobQueue,
      Serializable queueType, 
      MockWorkerCommand queuedJob, 
      long jobCreationTimeUnix) {
    super(jobQueue, queueType, queuedJob, jobCreationTimeUnix);
  }
}
