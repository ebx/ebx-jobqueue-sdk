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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.time.UnixTime;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * The type SNS job command test.
 *
 * @author eddspencer
 */
public class SNSJobCommandTest {

  private static final MockJobType JOB_TYPE = new MockJobType("SNS");
  private static final long CREATION_TIME = UnixTime.now();
  private static final String TOPIC_ARN = "ARN";

  @Mocked
  private SnsAsyncClient snsClient;

  private boolean processResultCalled;

  @Before
  public void setupMocks() {
    processResultCalled = false;
  }

  @Test
  public void doExecutePublishesMessage() throws Exception {
    final SNSJobCommand<Message, MockJobCommandExecutionContext> command =
        createCommand(new Message());

    expectMessage("{\"id\":\"Test\"}");

    final Future<JobSuccess> future = command.doExecute(null, 1L);
    assertTrue(future.get().isCompletedWithoutError());
  }

  @Test
  public void doExecutePublishesStringMessage() throws Exception {
    final SNSJobCommand<String, MockJobCommandExecutionContext> command =
        createCommand("Test message");

    final Future<JobSuccess> future = command.doExecute(null, 1L);
    assertTrue(future.get().isCompletedWithoutError());
  }

  @Test
  public void determineJobStatus() throws Exception {
    final SNSJobCommand<String, MockJobCommandExecutionContext> command =
        createCommand("Test message");

    final JobStatus jobStatusBefore = command.determineJobStatus();
    assertFalse(jobStatusBefore.isCompleted());

    command.doExecute(null, 1L);

    final JobStatus jobStatusAfter = command.determineJobStatus();
    assertTrue(jobStatusAfter.isCompleted());
  }

  private <T extends Serializable> SNSJobCommand<T, MockJobCommandExecutionContext> createCommand(
      T message) {
    return new SNSJobCommand<T, MockJobCommandExecutionContext>(JOB_TYPE, snsClient, CREATION_TIME,
        TOPIC_ARN) {
      @Override
      protected T getMessage() {
        return message;
      }

      @Override
      protected void processResult(PublishResponse result) {
        processResultCalled = true;
      }
    };
  }

  private void expectMessage(String expectedMessage) {
    new Expectations() {
      {
        snsClient.publish((PublishRequest) any);
        result = new Delegate() {
          PublishResponse publish(PublishRequest publishRequest) {
            assertEquals(TOPIC_ARN, publishRequest.topicArn());
            assertEquals(expectedMessage, publishRequest.message());

            return PublishResponse.builder().build();
          }
        };
      }
    };
  }

  /**
   * Test message
   *
   * @author eddspencer
   */
  private static class Message implements Serializable {
    private final String id = "Test";
  }

}
