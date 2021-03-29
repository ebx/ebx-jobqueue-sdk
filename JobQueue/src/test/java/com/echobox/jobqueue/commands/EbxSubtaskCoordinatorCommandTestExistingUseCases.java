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
 * Contains the test scenarios for subtask coordinator command that are already in use
 * in the codebase.
 *
 * The first three attributes of SubtaskCoordinatorBehaviour have 8 combinations
 * - numbered 1 to 8.   The forth attribute of SubtaskCoordinatorBehaviour
 *  ( IfRetriedBeforeCompletion ) has 4 possible values, ordered a to d.
 *
 * @author Michael Lavelle
 */
public class EbxSubtaskCoordinatorCommandTestExistingUseCases
    extends EbxSubtaskCoordinatorCommandTestBase {

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, true, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, false, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, false, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, true, false, true);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, false, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 7 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour7a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour7, false, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 8 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour8, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, true, false, false);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 5 a.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5a()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT);

    assertExpectedBehaviour(behaviour5, false, false, false);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, false, true);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, false, false);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 5 c.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5c()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, false, false);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, true, true);
  }

  /**
   * Test asynchronous subtasks still running one subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningOneSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, true, true);
  }

  /**
   * Test asynchronous subtasks all completed one subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testAsynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, true, true);
  }

  /**
   * Test synchronous subtasks still running one subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningOneSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, false, true);
  }

  /**
   * Test synchronous subtasks all completed one subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test(expected = IllegalStateException.class)
  public void testSynchronousSubtasksAllCompletedOneSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, false, true);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, true, true, false);
  }

  /**
   * Test asynchronous subtasks still running no subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksStillRunningNoSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, true, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 5 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour5d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour5 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.NEVER, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour5, false, true, false);
  }

  /**
   * Test asynchronous subtasks all completed no subtask failure behaviour 7 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testAsynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour7d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour7 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour7, false, true, false);
  }

  /**
   * Test synchronous subtasks still running no subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksStillRunningNoSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, true, false, false);
  }

  /**
   * Test synchronous subtasks all completed no subtask failure behaviour 8 d.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testSynchronousSubtasksAllCompletedNoSubtaskFailureBehaviour8d()
      throws InterruptedException {

    SubtaskCoordinatorBehaviour behaviour8 =
        new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
            InterruptWaitForCompletion.ON_FIRST_WARNING,
            IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR,
            IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);

    assertExpectedBehaviour(behaviour8, false, false, false);
  }
}
