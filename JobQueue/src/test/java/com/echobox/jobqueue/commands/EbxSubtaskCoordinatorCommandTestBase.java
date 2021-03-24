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
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobStatusType;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.time.UnixTime;
import org.javatuples.Pair;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Base class for tests that test subtask coordinator scenarios.
 *
 * @author Michael Lavelle
 */
public class EbxSubtaskCoordinatorCommandTestBase {

  private static Logger logger =
      LoggerFactory.getLogger(EbxSubtaskCoordinatorCommandTestBase.class);

  private static MockJobCommandExecutionContext context = new MockJobCommandExecutionContext();

  private String printBehaviour(SubtaskCoordinatorBehaviour behaviour) {
    String out = "";
    out += " " + behaviour.getRaiseWarningBehaviour();
    out += " " + behaviour.getInterruptionStrategy().completeWithErrorOnInterruption();
    out += " " + behaviour.getInterruptionStrategy().interruptOnPreCompletionWarning();
    out += " " + behaviour.getIfRetriedBeforeCompletionBehaviour();

    return out;
  }

  /**
   * Asserts the expected first execution and retry behaviour of a coordinator command
   * given the list of subtask mocks, the test scenario, and the desired coordinator behaviour
   *
   * @param behaviour The expected coordinator behaviour
   * @param subtasksStillRunning Whether the subtasks are still running in this test scenario
   * @param asyncSubtasks Whether the subtasks are asynchronous in this test scenario
   * @param oneSubtaskError Whether we have already had one subtask error on first execution in
   *                        this test scenario
   * @throws InterruptedException the interrupted exception
   */
  protected void assertExpectedBehaviour(SubtaskCoordinatorBehaviour behaviour,
      boolean subtasksStillRunning, boolean asyncSubtasks, boolean oneSubtaskError)
      throws InterruptedException {

    context.setEnableLoggingForAllJobTypes(true);

    MockWorkerCommand subtask1 =
        new MockWorkerCommand(UnixTime.now(), oneSubtaskError, false);
    MockWorkerCommand subtask2 =
        new MockWorkerCommand(UnixTime.now(), false, subtasksStillRunning);

    List<MockWorkerCommand> commands = Arrays.asList(subtask1, subtask2);

    SubtaskCoordinatorJobCommand<MockJobCommandExecutionContext, ?> command =
        asyncSubtasks ? new ExampleAsyncSubtaskCoordinatorCommand(UnixTime.now(), commands,
            behaviour)
            : new ExampleSynchronousSubtaskCoordinatorCommand(UnixTime.now(), commands,
                behaviour);

    Pair<JobStatusType, Boolean> expectedStatusPair =
        getExpectedStatusAndInterruptedFlag(subtasksStillRunning, oneSubtaskError,
            behaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError(),
            behaviour.getInterruptionStrategy().interruptOnPreCompletionWarning(),
            behaviour.getInterruptionStrategy().completeWithErrorOnInterruption(), asyncSubtasks);

    JobStatus status = null;
    boolean exceptionThrown = false;
    boolean nonTimeoutExceptionThrown = false;

    try {
      Future<JobSuccess> futureStatus = command.executeAsynchronously(context, 2);
      status = futureStatus.get();

    } catch (Exception e) {
      if (!(e.getCause() instanceof TimeoutException)) {
        nonTimeoutExceptionThrown = true;
      }
      exceptionThrown = true;
      status = command.determineJobStatus();
    }

    boolean exceptionExpected =
        subtasksStillRunning || (oneSubtaskError && behaviour.getRaiseWarningBehaviour()
            .raiseWarningOnSubtaskError());

    boolean nonTimeoutExceptionExpected =
        (exceptionExpected && !subtasksStillRunning) || (subtasksStillRunning && expectedStatusPair
            .getValue1());

    Assert.assertEquals(exceptionExpected, exceptionThrown);
    Assert.assertEquals(nonTimeoutExceptionExpected, nonTimeoutExceptionThrown);

    if (expectedStatusPair.getValue0() != status.getStatusType()) {
      logger.info(
          "FAILED:" + printBehaviour(behaviour) + " Expected:" + expectedStatusPair.getValue0()
              + " actual:" + status.getStatusType());
    }
    Assert.assertEquals(expectedStatusPair.getValue0(), status.getStatusType());

    if (subtasksStillRunning) {
      // Expect that the first subtask has completed, and that the second hasn't
      Assert.assertTrue(subtask1.getJobCompletionStatus().isCompleted());
      Assert.assertFalse(subtask2.getJobCompletionStatus().isCompleted());
    } else {
      // Expect that both have completed
      Assert.assertTrue(subtask1.getJobCompletionStatus().isCompleted());
      Assert.assertTrue(subtask2.getJobCompletionStatus().isCompleted());

      // Assert that the second has completed without error
      Assert.assertTrue(subtask2.getJobCompletionStatus().isCompletedWithoutError());
    }

    // Assert that the first has completed with error if oneSubtaskError, otherwise,
    // completed successfully
    if (oneSubtaskError) {
      Assert.assertTrue(subtask1.getJobCompletionStatus().isCompletedWithError());
    } else {
      Assert.assertTrue(subtask1.getJobCompletionStatus().isCompletedWithoutError());
    }

    if (asyncSubtasks) {

      // Assert that both subtasks have had execution attempted once
      Assert.assertEquals(1, subtask1.getNumberOfExecutionAttempts());
      Assert.assertEquals(1, subtask2.getNumberOfExecutionAttempts());

    } else {
      // For synchronous, Assert that first subtask has had execution attempted once
      Assert.assertEquals(1, subtask1.getNumberOfExecutionAttempts());
      if (oneSubtaskError && behaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError()
          && behaviour.getInterruptionStrategy().interruptOnPreCompletionWarning()) {
        Assert.assertEquals(0, subtask2.getNumberOfExecutionAttempts());
      } else {
        Assert.assertEquals(1, subtask2.getNumberOfExecutionAttempts());
      }
    }

    // Retry the coordinator and assert expected behaviour
    assertExpectedRetryBehaviour(command, commands, subtasksStillRunning, asyncSubtasks,
        oneSubtaskError, behaviour);

  }

  /**
   * Asserts the expected retry behaviour of a coordinator command that has already been executed
   * given the list of subtask mocks, the test scenario, and the desired coordinator behaviour
   *
   * @param command The coordinator command
   * @param subtasks The mock subtasks
   * @param subtasksStillRunning Whether the subtasks are still running in this test scenario
   * @param asyncSubtasks Whether the subtasks are asynchronous in this test scenario
   * @param oneSubtaskError Whether we have already had one subtask error on first execution
   * in this test scenario
   * @param behaviour The desired coordinator behaviour
   * @throws InterruptedException
   */
  private void assertExpectedRetryBehaviour(
      SubtaskCoordinatorJobCommand<MockJobCommandExecutionContext, ?> command,
      List<MockWorkerCommand> subtasks, boolean subtasksStillRunning, boolean asyncSubtasks,
      boolean oneSubtaskError, SubtaskCoordinatorBehaviour behaviour) throws InterruptedException {

    MockWorkerCommand subtask1 = subtasks.get(0);
    MockWorkerCommand subtask2 = subtasks.get(1);

    // We have asserted these are the values we expect above
    int beforeRetrySubtask1ExecutionAttempts = subtask1.getNumberOfExecutionAttempts();
    int beforeRetrySubtask2ExecutionAttempts = subtask2.getNumberOfExecutionAttempts();
    boolean firstSubtaskCompletedBeforeRetry = subtask1.getJobCompletionStatus().isCompleted();
    boolean firstSubtaskCompletedWithErrorBeforeRetry =
        subtask1.getJobCompletionStatus().isCompletedWithError();
    boolean secondSubtaskCompletedBeforeRetry = subtask2.getJobCompletionStatus().isCompleted();
    boolean secondSubtaskCompletedWithErrorBeforeRetry =
        subtask2.getJobCompletionStatus().isCompletedWithError();

    boolean completedBeforeRetry = command.getJobCompletionStatus().isCompleted();

    boolean expectedCompletedBeforeRetry = !subtasksStillRunning;

    if (subtasksStillRunning && oneSubtaskError && behaviour.getRaiseWarningBehaviour()
        .raiseWarningOnSubtaskError() && behaviour.getInterruptionStrategy()
        .interruptOnPreCompletionWarning() && behaviour.getInterruptionStrategy()
        .completeWithErrorOnInterruption()) {
      expectedCompletedBeforeRetry = true;
    }

    // Assert this is what we expect
    Assert.assertEquals(expectedCompletedBeforeRetry, completedBeforeRetry);

    // Retry the coordinator job
    try {
      Future<JobSuccess> futureStatus = command.executeAsynchronously(context, 1);
      futureStatus.get();
    } catch (Exception e) {
      command.determineJobStatus();
    }

    // Did we retry before completion?
    if (!completedBeforeRetry) {

      // If we retried before completion, behaviour is dependent on the
      // specified IfRetriedBeforeCompletion behaviour

      IfRetriedBeforeCompletion ifRetriedBehaviour =
          behaviour.getIfRetriedBeforeCompletionBehaviour();

      if (ifRetriedBehaviour.equals(IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT)) {

        // No more retry attempts
        Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts,
            subtask1.getNumberOfExecutionAttempts());

        if (beforeRetrySubtask2ExecutionAttempts == 0) {
          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts + 1,
              subtask2.getNumberOfExecutionAttempts());
        } else {
          // Subtask 2 still running, so assert no more retry attempts

          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts,
              subtask2.getNumberOfExecutionAttempts());
        }

      } else if (ifRetriedBehaviour.equals(IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS)) {

        boolean secondTaskToBeReattempted = true;
        if (!asyncSubtasks) {
          // Synchronous subtasks - the second task will not be attempted
          // if the first errors and we terminate
          if (oneSubtaskError && behaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError()
              && behaviour.getInterruptionStrategy().interruptOnPreCompletionWarning()) {
            secondTaskToBeReattempted = false;
          }
        }

        Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts + 1,
            subtask1.getNumberOfExecutionAttempts());

        if (secondTaskToBeReattempted) {
          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts + 1,
              subtask2.getNumberOfExecutionAttempts());
        } else {
          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts,
              subtask2.getNumberOfExecutionAttempts());
        }

      } else if (ifRetriedBehaviour
          .equals(IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS)) {

        if (firstSubtaskCompletedBeforeRetry) {
          Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts + 1,
              subtask1.getNumberOfExecutionAttempts());
        } else {
          Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts,
              subtask1.getNumberOfExecutionAttempts());
        }

        if (secondSubtaskCompletedBeforeRetry) {
          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts + 1,
              subtask2.getNumberOfExecutionAttempts());
        } else {
          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts,
              subtask2.getNumberOfExecutionAttempts());
        }

      } else if (ifRetriedBehaviour
          .equals(IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS)) {

        if (oneSubtaskError) {

          if (firstSubtaskCompletedWithErrorBeforeRetry) {
            Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts + 1,
                subtask1.getNumberOfExecutionAttempts());
          } else {
            Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts,
                subtask1.getNumberOfExecutionAttempts());
          }

          if (secondSubtaskCompletedWithErrorBeforeRetry) {
            Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts + 1,
                subtask2.getNumberOfExecutionAttempts());
          } else {
            Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts,
                subtask2.getNumberOfExecutionAttempts());
          }

        } else {

          Assert.assertEquals(beforeRetrySubtask1ExecutionAttempts,
              subtask1.getNumberOfExecutionAttempts());

          Assert.assertEquals(beforeRetrySubtask2ExecutionAttempts,
              subtask2.getNumberOfExecutionAttempts());

        }

      }
    } else {
      // Job was retried after completion,
      // behaviour is to retry all subtasks that aren't still running

      if (asyncSubtasks) {
        Thread.sleep(1000);
        // First subtask will have completed, so assert it has been attempted twice

        Assert.assertEquals(2, subtask1.getNumberOfExecutionAttempts());
        if (subtasksStillRunning) {
          // If subtask2 still running, assert it has only been attempted once
          Assert.assertEquals(1, subtask2.getNumberOfExecutionAttempts());
        } else {
          // If subtask2 has completed, assert it has been attempted twice
          Assert.assertEquals(2, subtask2.getNumberOfExecutionAttempts());
        }
      } else {

        // For sync subtasks, if the first subtask errors, and we interrupt on first subtask
        // error, the second task will never execute.

        if (oneSubtaskError && behaviour.getRaiseWarningBehaviour().raiseWarningOnSubtaskError()
            && behaviour.getInterruptionStrategy().interruptOnPreCompletionWarning()) {

          // Second subtask never executes - assert that first has been attempted twice
          Thread.sleep(1000);
          Assert.assertEquals(2, subtask1.getNumberOfExecutionAttempts());
          Assert.assertEquals(0, subtask2.getNumberOfExecutionAttempts());
        } else {

          // Second subtask will execute as we aren't interrupting
          // - assert that first and second have both been attempted twice
          Thread.sleep(1000);
          Assert.assertEquals(2, subtask1.getNumberOfExecutionAttempts());
          Assert.assertEquals(2, subtask2.getNumberOfExecutionAttempts());
        }
      }
    }
  }

  /**
   * Defines the expected status of a coordinator job, and a flag to indication
   * whether the job was interrupted, at a given time, given various conditions
   *
   * @param subtasksStillRunning the subtasks still running
   * @param atLeastOneSubtaskErrorSoFar the at least one subtask error so far
   * @param raiseWarningsOnSubtaskError the raise warnings on subtask error
   * @param interruptExecutionOnWarning the interrupt execution on warning
   * @param completeWithErrorOnInterruption the complete with error on interruption
   * @param asynchronousSubtasks the asynchronous subtasks
   * @return The expected status given the conditions
   * @throws IllegalStateException If the combination of conditions is not possible
   */
  public Pair<JobStatusType, Boolean> getExpectedStatusAndInterruptedFlag(
      boolean subtasksStillRunning, boolean atLeastOneSubtaskErrorSoFar,
      boolean raiseWarningsOnSubtaskError, boolean interruptExecutionOnWarning,
      boolean completeWithErrorOnInterruption, boolean asynchronousSubtasks)
      throws IllegalStateException {

    // First of all, verify that the flags passed in to this method are consistent with
    // each other and the mocks - this ensures we can't run tests for impossible scenarios

    if (asynchronousSubtasks) {

      // Async subtasks
      if (!subtasksStillRunning && raiseWarningsOnSubtaskError && atLeastOneSubtaskErrorSoFar
          && interruptExecutionOnWarning) {
        throw new IllegalStateException(
            "This is not a possible combination to test with" + " the mock commands - "
                + " subtasks will still be running if we are "
                + "configured to interrupt on first subtask error"
                + " and a subtask error has occurred");
      }
    } else {
      if (!subtasksStillRunning && raiseWarningsOnSubtaskError && atLeastOneSubtaskErrorSoFar
          && interruptExecutionOnWarning) {
        throw new IllegalStateException(
            "This is not a possible combination to test with" + " the mock commands - "
                + " subtasks will still be running if we are "
                + "configured to interrupt on first subtask error"
                + " and a subtask error has occurred");
      }
    }

    // Either subtasks are still running, or they aren't - logic is split by this distinction
    if (subtasksStillRunning) {

      // Defines the "runtime status" of a coordinator job whose subtasks haven't all completed yet
      boolean warningRaised = false;

      // If we raise warnings on subtask error:
      if (raiseWarningsOnSubtaskError) {
        // If a subtask has errored, raise warning
        warningRaised = atLeastOneSubtaskErrorSoFar;
        // If raising a warning causes us to interrupt
        if (warningRaised && interruptExecutionOnWarning) {
          // Interrupt the execution, and return status according to ifInterruptedBehaviour
          if (completeWithErrorOnInterruption) {
            return new Pair<JobStatusType, Boolean>(JobStatusType.COMPLETED_WITH_ERROR, true);
          } else {
            return new Pair<JobStatusType, Boolean>(JobStatusType.UNCOMPLETED_WITH_WARNING, true);
          }
        } else if (warningRaised) {
          // If a warning has been raised, but we don't interrupt
          // Changed
          return new Pair<JobStatusType, Boolean>(JobStatusType.UNKNOWN, false);
        } else {
          return new Pair<JobStatusType, Boolean>(JobStatusType.UNKNOWN, false);
        }
      } else {
        return new Pair<JobStatusType, Boolean>(JobStatusType.UNKNOWN, false);

      }
    } else {
      // The coordinator has "completed successfully" if any subtask has failed, otherwise
      // it has "completed unsuccessfully"
      if (atLeastOneSubtaskErrorSoFar) {
        if (raiseWarningsOnSubtaskError) {

          return new Pair<JobStatusType, Boolean>(JobStatusType.COMPLETED_WITH_ERROR, false);
        } else {
          return new Pair<JobStatusType, Boolean>(JobStatusType.COMPLETED_WITHOUT_ERROR, false);
        }
      } else {
        return new Pair<JobStatusType, Boolean>(JobStatusType.COMPLETED_WITHOUT_ERROR, false);
      }
    }
  }
}
