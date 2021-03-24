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
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.IfRetriedBeforeCompletion;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.IfWaitForCompletionIsInterrupted;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.InterruptWaitForCompletion;
import com.echobox.jobqueue.commands.behaviour.SubtaskCoordinatorBehaviour.RaiseWarning;
import org.junit.Test;

/**
 * Contains the test scenarios for subtask coordinator command that are not
 * currently being used in the codebase
 *
 * The first three attributes of SubtaskCoordinatorBehaviour have 8 combinations
 * - numbered 1 to 8.   The forth attribute of SubtaskCoordinatorBehaviour
 *  ( IfRetriedBeforeCompletion ) has 4 possible values, ordered a to d.
 *
 * @author Michael Lavelle
 */
public class EbxSubtaskCoordinatorCommandTestUnusedUseCases
    extends EbxSubtaskCoordinatorCommandTestBase {

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, true, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, true, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, true, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, true, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 1 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour1a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour1, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 2 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour2a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour2, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 3 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour3a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour3, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 4 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour4a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour4, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 6 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour6a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour6, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, false, false, false);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 1 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour1b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 2 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour2b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 3 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour3b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 4 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour4b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 6 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour6b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, false, false);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 1 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour1c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 2 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour2c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 3 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour3c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 4 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour4c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 6 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour6c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, false, false);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, true, true);
  }

  /**
   * Test asynchronous subtasks completed one subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedOneSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, false, true);
  }

  /**
   * Test synchronous subtasks completed one subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksCompletedOneSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, true, false);
  }

  /**
   * Test asynchronous subtasks completed no subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksCompletedNoSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, true, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 1 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour1d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour1 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour1, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 2 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour2d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour2 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER, InterruptWaitForCompletion.NEVER,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour2, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 3 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour3d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour3 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour3, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 4 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour4d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour4 = new SubtaskCoordinatorBehaviour(RaiseWarning.NEVER,
        InterruptWaitForCompletion.ON_FIRST_WARNING,
        IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour4, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 6 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour6d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour6 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour6, false, false, false);
  }

  /**
   * Test synchronous subtasks completed no subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksCompletedNoSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, false, false);
  }

  // Moved to here

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, false, true);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, false, false);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 5 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, false, false);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, false, true);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, false, true);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, false, false);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, false, false);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, true, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 7 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour7b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 7 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, true, true);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, false, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, true, false, false);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, false, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, true, false, true);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, false, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, true, false);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, false, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 8 b.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8b()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, true, true);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, false, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, false, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, true, false);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, false, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, false, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 8 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, true, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, true, false);
  }
}
