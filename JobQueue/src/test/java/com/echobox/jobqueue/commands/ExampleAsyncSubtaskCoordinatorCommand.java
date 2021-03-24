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

import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour;
import com.echobox.time.UnixTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Example async subtask coordinator command.
 * 
 * @author Michael Lavelle
 */
public class ExampleAsyncSubtaskCoordinatorCommand extends
    SubtaskCoordinatorJobCommand<MockJobCommandExecutionContext,
        AsynchronousJobCommandAdapter<MockJobCommandExecutionContext, MockWorkerCommand>> {

  /**
   *  Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private static Logger logger =
      LoggerFactory.getLogger(ExampleAsyncSubtaskCoordinatorCommand.class);
  
  private SubtaskCoordinatorBehaviour behaviour;
  private List<MockWorkerCommand> subtasks;

  /**
   * Instantiates a new Example async subtask coordinator command.
   *
   * @param jobCreationTimeUnix the job creation time unix
   * @param subtasks the subtasks
   * @param behaviour the behaviour
   */
  protected ExampleAsyncSubtaskCoordinatorCommand(long jobCreationTimeUnix,
      List<MockWorkerCommand> subtasks, SubtaskCoordinatorBehaviour behaviour) {
    super(new MockJobType("asyncCoordinator"), jobCreationTimeUnix);
    this.behaviour = behaviour;
    this.subtasks = subtasks;
  }
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand
   * #createSubtaskCoordinatorBehaviour()
   */
  @Override
  public SubtaskCoordinatorBehaviour createSubtaskCoordinatorBehaviour() {
    return behaviour;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#updateSubtasksBeforeRetry(
   * com.echobox.jobqueue.context.JobCommandExecutionContext, long)
   */
  @Override
  protected void updateSubtasksBeforeRetry(MockJobCommandExecutionContext executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {
    // NO-OP by default - subclasses can override this method if the list of subtasks need to 
    // be updated on each execution attempt
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#
   * createSubtasks(com.echobox.jobqueue.context.JobCommandExecutionContext, long)
   */
  @Override
  protected List<AsynchronousJobCommandAdapter<MockJobCommandExecutionContext,
      MockWorkerCommand>> createSubtasks(
      MockJobCommandExecutionContext executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {

    List<AsynchronousJobCommandAdapter<MockJobCommandExecutionContext, MockWorkerCommand>>
        asyncTasks =
        new ArrayList<>();
    for (MockWorkerCommand subtask : subtasks) {
      asyncTasks.add(
          new AsynchronousJobCommandAdapter<>(
              subtask, UnixTime.now()));
    }
    return asyncTasks;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand#getLogger()
   */
  @Override
  protected Logger getLogger() {
    return logger;
  }
}
