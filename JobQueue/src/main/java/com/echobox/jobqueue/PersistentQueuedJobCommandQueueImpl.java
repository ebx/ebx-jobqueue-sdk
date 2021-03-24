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

package com.echobox.jobqueue;

import com.echobox.jobqueue.commands.DequeuedJobCommand;
import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.commands.QueuedJobCommand;
import com.echobox.jobqueue.config.QueueConfig;
import com.echobox.jobqueue.config.QueueConfigFactory;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.status.JobFailedStatus;
import com.echobox.jobqueue.status.JobStatus;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.jobqueue.status.JobUnknownStatus;
import com.echobox.time.UnixTime;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * A type of PersistentQueuedJobCommandQueue implementation which is backed by a 
 * PersistentJobCommandQueue - a simpler interface to implement. Jobs on the queue as are stored 
 * as instances of PersistentJobCommandOnQueue
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public class PersistentQueuedJobCommandQueueImpl<C extends 
    JobCommandExecutionContext<C, Q, I>, Q extends Serializable, I extends Serializable>
    implements PersistentQueuedJobCommandQueue<C, Q, I> {

  private PersistentJobCommandQueue<C, Q, I> persistentJobCommandQueue;

  private QueueConfigFactory<Q> queueConfigFactory;

  /**
   * Instantiates a new PersistentQueuedJobCommandQueueImpl which delegates to the 
   * provided PersistentJobCommandQueue implementation ( a simpler interface to 
   * implement)
   *
   * @param persistentJobCommandQueue The persistent JobCommand queue
   * @param queueConfigFactory the queue config factory
   */
  public PersistentQueuedJobCommandQueueImpl(PersistentJobCommandQueue<C, Q, I> 
      persistentJobCommandQueue,
      QueueConfigFactory<Q> queueConfigFactory) {
    this.persistentJobCommandQueue = persistentJobCommandQueue;
    this.queueConfigFactory = queueConfigFactory;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#setJobSuccessfulCompletion(
   * com.echobox.jobqueue.QueuedJobId, java.lang.String, long)
   */
  @Override
  public boolean setJobSuccessfulCompletion(QueuedJobId<Q, I> queuedJobId, String consumerId,
      long completedUnixTime) {
    return persistentJobCommandQueue.setJobSuccessfulCompletion(queuedJobId, consumerId, 
        completedUnixTime);
  }
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#setJobUnsuccessfulCompletion(
   * com.echobox.jobqueue.QueuedJobId, java.lang.Exception, java.lang.String, long)
   */
  @Override
  public boolean setJobUnsuccessfulCompletion(QueuedJobId<Q, I> queuedJobId, Exception exception,
      String consumerId, long completedUnixTime) {
    return persistentJobCommandQueue
        .setJobUnsuccessfulCompletion(queuedJobId, exception, consumerId, completedUnixTime);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentQueuedJobCommandQueue#determineJobStatus(
   * com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public JobStatus determineJobStatus(QueuedJobId<Q, I> queuedJobId) {
    PersistentJobOnQueue<Q, I, JobCommand<C>> queuedJob = 
        persistentJobCommandQueue.getJob(queuedJobId);

    if (queuedJob == null) {
      return new JobFailedStatus(
          new IllegalStateException("Job with id:" + queuedJobId + " doesn't exist on the queue"));

    } else {

      // Work out current completion status flags

      boolean completed = queuedJob.getJob().getJobCompletionStatus().isCompleted();
      boolean hasError = queuedJob.getJob().getExceptionMessage() != null;
      boolean isConsumed = queuedJob.getConsumerId() != null;

      QueueConfig queueConfig = queueConfigFactory.getQueueConfig(queuedJob.getId().getQueueType());

      // Set last heartbeat time to be the consumption time if the consumption time
      // was in the last (heartbeat interval * 2) seconds
      Long lastHeartbeatTimeUnix = queuedJob.getLastHeartbeatTimeUnix();
      if (queuedJob != null && lastHeartbeatTimeUnix == null
          && queuedJob.getConsumptionTimeUnix() != null && queuedJob.getConsumptionTimeUnix()
          > UnixTime.now() - queueConfig.getHeartbeatPulsePeriodSecs() * 2) {
        lastHeartbeatTimeUnix = queuedJob.getConsumptionTimeUnix();
      }

      // Require a heartbeat with the last 2 * heartbeatPulsePeriodSecs seconds
      long twoHeartbeatDurationSeconds = queueConfig.getHeartbeatPulsePeriodSecs() * 2;
      long requiredHeartbeatAfterUnixTime = UnixTime.now() - twoHeartbeatDurationSeconds;

      if (completed) {

        // If the job is completed, return the appropriate status
        if (hasError) {
          return new JobFailedStatus(
              new RuntimeException(queuedJob.getJob().getExceptionMessage()));
        } else {

          return new JobSuccess();
        }
      } else if (isConsumed) {

        // If the job has been consumed, but not completed always check the last heartbeat time
        if (lastHeartbeatTimeUnix == null
            || lastHeartbeatTimeUnix < requiredHeartbeatAfterUnixTime) {
          Exception noHeartbeatException = new TimeoutException(
              "No heartbeat in the last:" + twoHeartbeatDurationSeconds
                  + " seconds for queued job with id:" + queuedJobId);

          // Treat the job as effectively completed
          setJobUnsuccessfulCompletion(queuedJobId, noHeartbeatException, null,
              UnixTime.now());

          return new JobFailedStatus(noHeartbeatException);
        }
      }
    }
    return new JobUnknownStatus();
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentQueuedJobCommandQueue#resetJob(
   * com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public boolean resetJob(QueuedJobId<Q, I> queuedJobId) {
    return persistentJobCommandQueue.resetJob(queuedJobId);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteJob(com.echobox.jobqueue.
   * QueuedJobId)
   */
  @Override
  public boolean deleteJob(QueuedJobId<Q, I> queuedJobId) {
    return persistentJobCommandQueue.deleteJob(queuedJobId);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteAllUnconsumedJobs(
   * java.io.Serializable)
   */
  @Override
  public boolean deleteAllUnconsumedJobs(Q queueType) {
    return persistentJobCommandQueue.deleteAllUnconsumedJobs(queueType);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#reserveAllUnconsumedJobs(
   * java.io.Serializable, java.lang.String)
   */
  @Override
  public boolean reserveAllUnconsumedJobs(Q queueType, String producerId) {
    return persistentJobCommandQueue.reserveAllUnconsumedJobs(queueType, producerId);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobQueue#getNextJob(java.lang.String, java.io.Serializable)
   */
  @Override
  public Pair<QueuedJobId<Q, I>, DequeuedJobCommand<C, Q, I>> getNextJob(String consumerId,
      Q queueType) {
    
    Pair<QueuedJobId<Q, I>, PersistentJobCommandOnQueue<C, Q, I>> queuedJobNoSQLDataPair =
        persistentJobCommandQueue.getNextJob(consumerId, queueType);

    if (queuedJobNoSQLDataPair != null) {

      PersistentJobOnQueue<Q, I, JobCommand<C>> queuedJobNoSQLData =
          queuedJobNoSQLDataPair.getValue1();

      DequeuedJobCommand<C, Q, I> dequeuedJobCommand =
          new DequeuedJobCommand<>(this, queuedJobNoSQLData.getJob(),
              queuedJobNoSQLData.getJob().getJobCreationTimeUnix(), queuedJobNoSQLData.getId(),
              consumerId);

      return new Pair<>(queuedJobNoSQLData.getId(),
          dequeuedJobCommand);
    }

    return null;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#updateHeartbeatTime(
   * com.echobox.jobqueue.QueuedJobId, java.lang.String, long)
   */
  @Override
  public boolean updateHeartbeatTime(QueuedJobId<Q, I> queuedJobId, String consumerId,
      long heartbeatUnixTime) {
    return persistentJobCommandQueue.updateHeartbeatTime(queuedJobId, consumerId, 
        heartbeatUnixTime);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#getJob(com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public PersistentJobCommandOnQueue<C, Q, I> getJob(QueuedJobId<Q, I> queuedJobId) {
    return persistentJobCommandQueue.getJob(queuedJobId);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteAllJobs(java.io.Serializable)
   */
  @Override
  public boolean deleteAllJobs(Q queueType) {
    return persistentJobCommandQueue.deleteAllJobs(queueType);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobQueue#getJobCount(java.io.Serializable)
   */
  @Override
  public long getJobCount(Q queueType) {
    return persistentJobCommandQueue.getJobCount(queueType);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobQueue#addJobToQueue(com.echobox.jobqueue.Job, 
   * java.io.Serializable)
   */
  @Override
  public QueuedJobId<Q, I> addJobToQueue(QueuedJobCommand<C, Q, I, ?> queuedJobCommand, 
      Q queueType) {
    return persistentJobCommandQueue.addJobToQueue(queuedJobCommand.getQueuedJob(), queueType);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobQueue#addJobToQueueWithId(com.echobox.jobqueue.Job, 
   * com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public QueuedJobId<Q, I> addJobToQueueWithId(QueuedJobCommand<C, Q, I, ?> queuedJobCommand,
      QueuedJobId<Q, I> queuedJobId) {
    return persistentJobCommandQueue.addJobToQueueWithId(queuedJobCommand, queuedJobId);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#saveJob(
   * com.echobox.jobqueue.PersistentJobCommandOnQueue)
   */
  @Override
  public QueuedJobId<Q, I> saveJob(
      PersistentJobCommandOnQueue<C, Q, I> persistentJobCommandOnQueue) {
    return persistentJobCommandQueue.saveJob(persistentJobCommandOnQueue);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#getAllConsumedJobs(java.io.Serializable)
   */
  @Override
  public List<PersistentJobCommandOnQueue<C, Q, I>> getAllConsumedJobs(Q queueType) {
    return persistentJobCommandQueue.getAllConsumedJobs(queueType);
  }
}
