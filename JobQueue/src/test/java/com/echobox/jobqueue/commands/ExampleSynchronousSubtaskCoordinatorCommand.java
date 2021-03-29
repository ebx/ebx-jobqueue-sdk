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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The type Example synchronous subtask coordinator command.
 * 
 * @author Michael Lavelle
 */
public class ExampleSynchronousSubtaskCoordinatorCommand
    extends SubtaskCoordinatorJobCommand<MockJobCommandExecutionContext, MockWorkerCommand> {

  /**
   *  Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private SubtaskCoordinatorBehaviour behaviour;
  private List<MockWorkerCommand> subtasks;

  /**
   * Instantiates a new Example synchronous subtask coordinator command.
   *
   * @param jobCreationTimeUnix the job creation time unix
   * @param subtasks the subtasks
   * @param behaviour the behaviour
   */
  protected ExampleSynchronousSubtaskCoordinatorCommand(long jobCreationTimeUnix,
      List<MockWorkerCommand> subtasks, SubtaskCoordinatorBehaviour behaviour) {
    super(new MockJobType("synchronousCoordinator"), jobCreationTimeUnix);
    this.behaviour = behaviour;
    this.subtasks = subtasks;
  }

  private static Logger logger =
      LoggerFactory.getLogger(ExampleSynchronousSubtaskCoordinatorCommand.class);

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.SubtaskCoordinatorJobCommand
   * #createSubtaskCoordinatorBehaviour()
   */
  @Override
  public SubtaskCoordinatorBehaviour createSubtaskCoordinatorBehaviour() {
    return behaviour;
  }

  /*
   * (non-Javadoc)
   * 
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
  protected List<MockWorkerCommand> createSubtasks(MockJobCommandExecutionContext executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {

    return subtasks;
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
