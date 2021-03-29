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

package com.echobox.jobqueue.demo.commands;

import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.IfRetriedBeforeCompletion;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.IfWaitForCompletionIsInterrupted;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.InterruptWaitForCompletion;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.RaiseWarning;
import com.echobox.jobqueue.demo.domain.DemoJobTypeEnum;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import com.echobox.jobqueue.demo.domain.bindings.DemoCommandExecutionContext;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobCommand;
import com.echobox.jobqueue.demo.domain.bindings.DemoSubtaskCoordinatorCommandBase;
import com.echobox.time.UnixTime;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * This command coordinates a list of queued job commands - each of which wraps a DemoWorkCommand
 * instance to be executed on a consumer
 * 
 * @author Michael Lavelle
 *
 */
public class DemoCoordinatorCommand extends 
    DemoSubtaskCoordinatorCommandBase<DemoQueuedJobCommand<DemoWorkerCommand>> {

  private static Logger logger = LoggerFactory.getLogger(DemoCoordinatorCommand.class);
  
  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private List<String> messages;
  
  private PersistentQueuedJobCommandQueue<DemoCommandExecutionContext, DemoQueueType, 
      ObjectId> jobQueue;

  /**
   * Initialise DemoCoordinatorCommand
   * @param jobQueue The job queue we send the commands to
   * @param jobCreationTimeUnix The creation unix time of this coordinator
   * @param messages A list of messages - we want to create queued worker commands for each
   * of these messages
   */
  public DemoCoordinatorCommand(PersistentQueuedJobCommandQueue<DemoCommandExecutionContext, 
      DemoQueueType, ObjectId> jobQueue, long jobCreationTimeUnix, 
      String... messages) {
    super(DemoJobTypeEnum.DEMO_COORDINATOR_COMMAND, jobCreationTimeUnix);
    this.messages = Arrays.asList(messages);
    this.jobQueue = jobQueue;
  }


  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#createSubtasks(
   * com.echobox.jobqueue.context.JobCommandExecutionContext, long)
   */
  @Override
  protected List<DemoQueuedJobCommand<DemoWorkerCommand>> 
      createSubtasks(DemoCommandExecutionContext executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {
    
    List<DemoQueuedJobCommand<DemoWorkerCommand>> queuedWorkerCommands = new ArrayList<>();
    for (String message : messages) {
      queuedWorkerCommands.add(new DemoQueuedJobCommand<>(jobQueue, DemoQueueType.QUEUE_1,
          new DemoWorkerCommand(UnixTime.now(), message),
          maxAsyncExecutionCompletionWaitTimeoutSecs));
    }
    return queuedWorkerCommands;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#getLogger()
   */
  @Override
  protected Logger getLogger() {
    return logger;
  }
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#updateSubtasksBeforeRetry(
   * com.echobox.jobqueue.context.JobCommandExecutionContext, long)
   */
  @Override
  protected void updateSubtasksBeforeRetry(DemoCommandExecutionContext executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {
      // No op - we have a fixed set of tasks
  }
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#
   * 
   * createSubtaskCoordinatorBehaviour()
   */
  @Override
  public SubtaskCoordinatorBehaviour createSubtaskCoordinatorBehaviour() {
    return new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);
  }


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DemoCoordinatorCommand [messages=" + messages + "]";
  }
}
