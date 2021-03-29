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

import com.echobox.jobqueue.CoreJobType;
import com.echobox.jobqueue.JobQueue;
import com.echobox.jobqueue.PersistentJobCommandOnQueue;
import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.commands.status.FutureJobCommandSuccess;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.events.JobCommandStatsGatherer;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.jobqueue.status.JobUnknownStatus;
import com.echobox.jobqueue.status.ProgressStatsUnavailable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * JobCommand decorator which wraps a JobCommand we wish to queue instead of executing locally
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * @param <J> The type of JobCommand which is being queued
 * 
 * @author Michael Lavelle
 */
public class QueuedJobCommand<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable, J extends
    JobCommand<C>>
    extends CoordinatorJobCommand<C> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  /**
   * A logging instance for this class
   */
  private static Logger logger = LoggerFactory.getLogger(QueuedJobCommand.class);

  /**
   * The queue we are sending the job commands to.
   */
  private PersistentQueuedJobCommandQueue<C, Q, I> jobQueue;

  /**
   * The job we are queuing
   */
  private J queuedJob;

  /**
   * The type of queue 
   */
  private Q queueType;

  /**
   * The id
   */
  private QueuedJobId<Q, I> queuedJobId;

  /**
   * Instantiates a new Queued job command.
   *
   * @param jobQueue The jobQueue we will send the queuedJob to
   * @param queueType The type of queue we will send the queuedJob to
   * @param queuedJob The job we wish to queue
   * @param jobCreationTimeUnix the job creation time unix
   */
  public QueuedJobCommand(PersistentQueuedJobCommandQueue<C, Q, I> jobQueue, Q queueType, 
      J queuedJob,  long jobCreationTimeUnix) {
    super(new CoreJobCommandType(CoreJobType.QUEUED_JOB), jobCreationTimeUnix);
    this.jobQueue = jobQueue;
    this.queuedJob = queuedJob;
    this.queueType = queueType;
    if (JobCommandStatsGatherer.isEnabled()) {
      queuedJob.addCompletionListener(
          new JobCommandStatsGatherer<>(queueType, queuedJob.getJobCreationTimeUnix()));
    }
  }

  /**
   * Constructor for creating a queuedJob command where we already have the objectId
   *
   * @param jobQueue The jobQueue we will send the queuedJob to
   * @param queuedJob The job we wish to queue
   * @param jobCreationTimeUnix the job creation time unix
   * @param queuedJobId the queued job id
   */
  public QueuedJobCommand(PersistentQueuedJobCommandQueue<C, Q, I> jobQueue, J queuedJob, 
      long jobCreationTimeUnix, QueuedJobId<Q, I> queuedJobId) {
    super(new CoreJobCommandType(CoreJobType.QUEUED_JOB), jobCreationTimeUnix);
    this.jobQueue = jobQueue;
    this.queuedJob = queuedJob;
    this.queueType = queuedJobId.getQueueType();
    this.queuedJobId = queuedJobId;
  }

  /**
   * Gets queue type.
   *
   * @return The queueType
   */
  public Q getQueueType() {
    return queueType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isAsynchronousExecution()
   */

  /**
   * By default this JobCommand is asynchronous by nature of the fact that execution adds the
   * JobCommand to a JobCommandQueue for decoupled execution by consumers
   */
  @Override
  protected boolean isAsynchronousExecution() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#executeOrRetry(com.echobox.jobqueue.context.
   * JobCommandExecutionContext,
   * long)
   */

  /**
   * The default behaviour of this type of job is asynchronous - the JobCommand is added to 
   * the JobCommandQueue and a handle to the future JobSuccess status is returned, which
   * also notifies of any exceptions occurring that prevent JobSuccess being achieved
   */
  @Override
  protected Future<JobSuccess> doExecute(C executionContext,
      long defaultMaxFutureWaitTimeoutSeconds) throws Exception {
    if (queuedJobId == null) {
      queuedJobId = jobQueue.addJobToQueue(this, queueType);
    } else {
      boolean reset = jobQueue.resetJob(queuedJobId);
      if (!reset) {
        logger.warn(this.getLogMessage(executionContext, JobCommandEvent.JOB_ERROR,
            "Call to reset job " + "wasn't successful"));
      }
    }
    return new FutureJobCommandSuccess<C>(this, executionContext,
        defaultMaxFutureWaitTimeoutSeconds);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.CoordinatorJobCommand#getDelegatedCompletionStatus()
   */
  @Override
  protected JobStatus getDelegatedStatus() {
    if (this.getQueuedJobId() == null) {
      return new JobUnknownStatus();
    } else {
      JobStatus status = jobQueue.determineJobStatus(queuedJobId);

      // When the queued job completes ensure to call it's completion listeners, these have 
      // already been called on the consumer side but this allows for the producer and consumer 
      // to have different actions on complete.
      if (status.isCompleted()) {
        final PersistentJobCommandOnQueue<C, Q, I> completedJob = jobQueue.getJob(queuedJobId);
        if (completedJob != null) {
          final long completionTimeUnix = completedJob.getJob().getCompletedUnixTime();
          if (status.isCompletedWithoutError()) {
            queuedJob.setSuccessfulCompletionUnixTime(completionTimeUnix);
          } else if (status.isCompletedWithError()) {
            queuedJob.setUnsuccessfulCompletionUnixTime(completionTimeUnix, status.getException());
          }
        } else {
          logger.warn(String.format("Unable to read completed job with id: %s", queuedJobId));
        }
      }

      return status;
    }
  }

  /**
   * Gets queued job id.
   *
   * @return The id
   */
  public QueuedJobId<Q, I> getQueuedJobId() {
    return queuedJobId;
  }

  /**
   * Gets queued job.
   *
   * @return The job we are queuing
   */
  public J getQueuedJob() {
    return queuedJob;
  }

  /**
   * Gets job queue.
   *
   * @return The queue that we use to queue these jobs
   */
  public JobQueue<Q, I, QueuedJobCommand<C, Q, I, ?>, DequeuedJobCommand<C, Q, I>> getJobQueue() {
    return jobQueue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#cleanUp(com.echobox.jobqueue.
   * JobCommandExecutionContext)
   */
  @Override
  public void cleanUp(C executionContext) throws Exception {
    if (queuedJobId != null) {
      jobQueue.deleteJob(queuedJobId);
      queuedJobId = null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isCompletionStatusLoggingEnabled()
   */
  @Override
  public boolean isCompletionStatusLoggingEnabled(C context) {
    return queuedJob.isCompletionStatusLoggingEnabled(context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#createInterruptStrategy()
   */
  @Override
  public JobCommandInterruptionStrategy createInterruptStrategy() {
    return JobCommandInterruptionStrategy.NONE;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.JobCommand#getProgressReport()
   */
  @Override
  public JobProgressReport getProgressReport() {
    return new ProgressStatsUnavailable();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ":" + getJobType() + ":" + queuedJob.toString();
  }

  @Override
  public void resetCompletionStatus() {
    super.resetCompletionStatus();
    
    // Ensure to also reset decorated job
    queuedJob.resetCompletionStatus();
  }
}
