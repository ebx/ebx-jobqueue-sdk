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

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.jobqueue.status.JobUnknownStatus;
import com.echobox.jobqueue.status.ProgressStatsUnavailable;
import com.google.gson.Gson;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * Job command that sends the jobs as SNS messages to the configured SNS topic. Extend this class
 * and define you message deserialisation and topic ARN, when it it is executed it will send the
 * message to the SNS topic as fire and forget.
 *
 * @param <M>  the message type parameter
 * @param <C>  the job command execution context type parameter
 * @author eddspencer
 */
public abstract class SNSJobCommand<M extends Serializable,
    C extends JobCommandExecutionContext<C, ?, ?>>
    extends JobCommand<C> {
  
  /**
   * A serialiser for jobs
   */
  protected static final Gson GSON = new Gson();
  
  /**
   * A logging instance
   */
  protected static Logger logger = LoggerFactory.getLogger(SNSJobCommand.class);
  
  /**
   * The AWS SNS async client instance
   */
  protected final AmazonSNSAsync snsClient;
  
  /**
   * The SNS topic ARN to use
   */
  protected final String topicArn;
  
  /**
   * True if the message has been sent
   */
  protected boolean messageSent;

  /**
   * Instantiates a new SNS Job command.
   *
   * @param jobCommandType the job command type   
   * @param snsClient the SNS client 
   * @param jobCreationTimeUnix the job creation time unix   
   * @param topicArn the topic ARN
   */
  public SNSJobCommand(JobType<?> jobCommandType, AmazonSNSAsync snsClient,
      long jobCreationTimeUnix, String topicArn) {
    super(jobCommandType, jobCreationTimeUnix);
    this.topicArn = topicArn;
    this.snsClient = snsClient;
    this.messageSent = false;
  }

  /**
   * Creates the message body to send in SNS message
   *
   * @return message body
   * @throws Exception the exception
   */
  protected abstract M getMessage() throws Exception;

  /**
   * Process the result of sending the message
   * @param result
   */
  protected abstract void processResult(PublishResult result);

  @Override
  protected boolean isAsynchronousExecution() {
    return true;
  }

  @Override
  protected Future<JobSuccess> doExecute(C executionContext,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {
    try {
      final M message = getMessage();

      final String messageStr;
      if (message instanceof String) {
        messageStr = (String) message;
      } else {
        messageStr = GSON.toJson(message);
      }

      final PublishRequest publishReq =
          new PublishRequest().withTopicArn(topicArn).withMessage(messageStr);

      final PublishResult result = snsClient.publish(publishReq);
      processResult(result);

      this.messageSent = true;
      return ConcurrentUtils.constantFuture(new JobSuccess());
    } catch (Exception ex) {
      logger.error("Error sending SNS message", ex);
      throw ex;
    }
  }

  @Override
  public void cleanUp(C executionContext) throws Exception {
    // No-op by default
  }

  @Override
  public JobStatus determineJobStatus() {
    // Do not know the status as message is fire and forget, so assume completed once sent message
    if (messageSent) {
      return new JobSuccess();
    }
    return new JobUnknownStatus();
  }

  @Override
  public JobCommandInterruptionStrategy createInterruptStrategy() {
    return JobCommandInterruptionStrategy.NONE;
  }

  @Override
  public JobProgressReport getProgressReport() {
    // SNS message is fire and forget
    return new ProgressStatsUnavailable();
  }
}
