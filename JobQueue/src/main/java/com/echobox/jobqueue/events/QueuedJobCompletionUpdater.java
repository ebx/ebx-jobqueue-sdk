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

package com.echobox.jobqueue.events;

import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * A JobCommandCompletionListener that can be registered with DequeuedJobCommands or executing on
 * the consumer side or producers if required, responsible for updating the completion status on 
 * the PersistentQueuedJobCommandQueue.
 * 
 * 
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> =The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public class QueuedJobCompletionUpdater<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable> 
    implements JobCommandCompletionListener {

  private static Logger logger = LoggerFactory.getLogger(QueuedJobCompletionUpdater.class);

  private PersistentQueuedJobCommandQueue<C, Q, I> jobCommandQueue;
  private String consumerId;
  private QueuedJobId<Q, I> queuedJobId;

  /**
   * Completion updater for consumers
   *
   * @param jobCommandQueue the job command queue
   * @param consumerId the consumer id
   * @param queuedJobId the queued job id
   */
  public QueuedJobCompletionUpdater(PersistentQueuedJobCommandQueue<C, Q, I> jobCommandQueue, 
      String consumerId,  QueuedJobId<Q, I> queuedJobId) {
    this.jobCommandQueue = jobCommandQueue;
    this.consumerId = consumerId;
    this.queuedJobId = queuedJobId;
  }

  /**
   * Completion updater for producers
   *
   * @param jobCommandQueue the job queue
   * @param queuedJobId the queued job id
   */
  public QueuedJobCompletionUpdater(PersistentQueuedJobCommandQueue<C, Q, I> jobCommandQueue,
      QueuedJobId<Q, I> queuedJobId) {
    this.jobCommandQueue = jobCommandQueue;
    this.consumerId = null;
    this.queuedJobId = queuedJobId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.events.JobCommandCompletionListener#setSuccessfulCompletionUnixTime(
   * long)
   */
  @Override
  public void setSuccessfulCompletionUnixTime(long completedUnixTime) {
    boolean updated =
        jobCommandQueue.setJobSuccessfulCompletion(queuedJobId, consumerId, completedUnixTime);
    if (!updated) {
      Object job = jobCommandQueue.getJob(queuedJobId);
      if (job != null) {
        logger.error("Did not manage to " + "set successful job completion time on:" + queuedJobId
            .getJobId());
      } else {
        logger.warn(
            "Did not manage to " + "set successful job completion time on:" + queuedJobId.getJobId()
                + " as it no " + " longer exists on the queue");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.events.JobCommandCompletionListener#
   * setUnsuccessfulCompletionUnixTime(
   * long, java.lang.Exception)
   */
  @Override
  public void setUnsuccessfulCompletionUnixTime(long completedUnixTime, Exception exception) {
    boolean updated = jobCommandQueue
        .setJobUnsuccessfulCompletion(queuedJobId, exception, consumerId, completedUnixTime);
    if (!updated) {
      Object job = jobCommandQueue.getJob(queuedJobId);
      if (job != null) {
        logger.error("Did not manage to " + "set unsuccessful job completion time on:" + queuedJobId
            .getJobId());
      } else {
        logger.warn("Did not manage to " + "set unsuccessful job completion time on:" + queuedJobId
            .getJobId() + " as it no " + " longer exists on the queue");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.events.JobCommandCompletionListener#resetCompletionStatus()
   */
  @Override
  public void resetCompletionStatus() {
    boolean reset = jobCommandQueue.resetJob(queuedJobId);
    if (!reset) {
      Object job = jobCommandQueue.getJob(queuedJobId);
      if (job != null) {
        logger.error("Did not manage to " + "reset queued job:" + queuedJobId.getJobId());
      } else {
        logger.warn("Did not manage to " + "reset completion time on:" + queuedJobId.getJobId()
            + " as it no " + " longer exists on the queue");
      }
    }
  }
}
