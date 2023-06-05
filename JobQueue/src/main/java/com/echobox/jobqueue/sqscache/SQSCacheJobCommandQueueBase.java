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

package com.echobox.jobqueue.sqscache;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.echobox.cache.CacheService;
import com.echobox.jobqueue.PersistentJobCommandOnQueue;
import com.echobox.jobqueue.PersistentJobCommandQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.time.UnixTime;
import com.google.gson.reflect.TypeToken;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A base implementation of PersistentJobCommandQueue which uses SQS for queueing and a 
 * CacheService for persistence
 * 
 * @author Michael Lavelle
 * 
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 */
public abstract class SQSCacheJobCommandQueueBase<C 
    extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable> 
    implements PersistentJobCommandQueue<C, Q, I>  {

  /**
   * A logging instance for this class
   */
  private static Logger logger = LoggerFactory.getLogger(SQSCacheJobCommandQueueBase.class);

  private final CacheService cacheService;
  private final AmazonSQS sqs;
  private final String cacheKeyPrefix;
  private final int cacheExpiryTimeSeconds;

  /**
   * The constructor
   * @param sqs The SQS client
   * @param cacheService The cache service
   * @param cacheKeyPrefix The prefix we use for keys in the cache
   * @param cacheExpiryTimeSeconds The number of seconds that the persistent job commands stored
   * in the cache exist for without expiring.  This time should be large enough to include the
   * time spent on the queue before consumption.  In this implementation the heartbeat update
   * on the consumer will extend the lifetime of the persistent job by cacheExpiryTimeSeconds
   * every heartbeat after consumption. 
   */
  public SQSCacheJobCommandQueueBase(AmazonSQS sqs, CacheService cacheService, 
      String cacheKeyPrefix, int cacheExpiryTimeSeconds) {
    this.cacheService = cacheService;
    this.sqs = sqs;
    this.cacheKeyPrefix = cacheKeyPrefix;
    this.cacheExpiryTimeSeconds = cacheExpiryTimeSeconds;
  }
  
  private void addToRepository(QueuedJobId<Q, I> key,
      PersistentJobCommandOnQueue<C, Q, I> 
      jobCommand) {

    boolean added =
        cacheService.trySaveItemToCache(getCacheKey(key), cacheExpiryTimeSeconds, jobCommand);

    if (!added) {
      throw new RuntimeException("Unable to add job command to cache");
    }

  }
  
  /**
   * A getter for the cache key
   * @param queuedJobId The queued job id
   * @return The cache key for this queued job id
   */
  protected String getCacheKey(QueuedJobId<Q, I> queuedJobId) {
    return cacheKeyPrefix + "-"
        + queuedJobId.getQueueType() + "-" + queuedJobId.getJobId();
  }
 
  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#addJobToQueue(com.echobox.jobqueue.Job, java.lang.Object)
   */
  @Override
  public QueuedJobId<Q, I> addJobToQueue(
      JobCommand<C> job, Q queueType) {

    I generatedJobId = generateNewJobId();

    QueuedJobId<Q, I> queuedJobId = createQueuedJobId(queueType, generatedJobId);
    PersistentJobCommandOnQueue<C, Q, I> command = createPersistentJobCommandOnQueue(queuedJobId,
        job);

    addToRepository(queuedJobId, command);

    addToQueue(queuedJobId);

    return queuedJobId;
  }
  
  /**
   * Adds the job in the queue 
   * @param queuedJobId if of the job queue
   */
  protected void addToQueue(QueuedJobId<Q, I> queuedJobId) {

    SendMessageRequest sendMessageRequest = new SendMessageRequest()
        .withQueueUrl(getSQSQueueUrl(queuedJobId.getQueueType()))
        .withMessageBody(createMessageBodyFromJobId(queuedJobId.getJobId()))
        .withDelaySeconds(5);
    
    sqs.sendMessage(sendMessageRequest);
    
  }

  /**
   * Generates new job id
   * @return A new generated job id
   */
  protected abstract I generateNewJobId();
  
  /**
   * Creates a Queue Job Id
   * @param queueType The queue type
   * @param jobId The job id
   * @return An instantiated QueuedJobId for this queue type and job id
   */
  protected abstract QueuedJobId<Q, I> createQueuedJobId(Q queueType, I jobId);
  
  /**
   * Persists the job command on queue
   * @param queuedJobId The queued job id we wish to create a PersistentJobCommandOnQueue for
   * @param jobCommand The job command
   * @return An instantiated PersistentJobCommandOnQueue for this jobCommand and queuedJobid
   */
  protected abstract PersistentJobCommandOnQueue<C, Q, I> createPersistentJobCommandOnQueue(
      QueuedJobId<Q, I> queuedJobId,
      JobCommand<C> jobCommand);
  
  /**
   * A getter of PersistentJobCommandOnQueue we are persisting
   * @return The class of the type of PersistentJobCommandOnQueue we are persisting
   */
  protected abstract TypeToken<?> getPersistentJobCommandOnQueueType();

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#addJobToQueueWithId(com.echobox.jobqueue.Job,
   * com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public QueuedJobId<Q, I> addJobToQueueWithId(
      JobCommand<C> job,
      QueuedJobId<Q, I> queuedJobId) {

    PersistentJobCommandOnQueue<C, Q, I> command = 
        createPersistentJobCommandOnQueue(queuedJobId, job);

    addToRepository(queuedJobId, command);

    addToQueue(queuedJobId);

    return queuedJobId;
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#getNextJob(java.lang.String, java.lang.Object)
   */
  @Override
  public Pair<QueuedJobId<Q, I>, 
      PersistentJobCommandOnQueue<C, Q, I>> getNextJob(
      String consumerId, Q queueType) {
    I jobId = pollQueue(queueType);

    QueuedJobId<Q, I> queuedJobId = createQueuedJobId(queueType, jobId);
    
    PersistentJobCommandOnQueue<C, Q, I> 
        persistentJobCommand = getJob(queuedJobId);
    
    if (persistentJobCommand != null) {

      persistentJobCommand.setConsumerId(consumerId);
      persistentJobCommand.setConsumptionTimeUnix(UnixTime.now());
      saveJob(persistentJobCommand);

      return new Pair<>(persistentJobCommand.getId(), persistentJobCommand);
    } else {
      return null;
    }

  }
  
  /**
   * Getter to obtain the SQS queue url for
   * @param queueType The queue type to obtain the SQS queue url for
   * @return The SQS queue url
   */
  protected abstract String getSQSQueueUrl(Q queueType);

  /**
   * Polls an message form the queue
   * @param queueType queue type
   * @return latest message from the queue
   */
  private I pollQueue(Q queueType) {
   
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
        .withQueueUrl(getSQSQueueUrl(queueType))
        .withMaxNumberOfMessages(1);
    List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    
    if (messages.isEmpty()) {
      return null;
    } else if (messages.size() > 1) {
      throw new IllegalStateException("Multiple messages retreived from SQS");
    } else {
      Message message = messages.get(0);
      return getJobIdFromMessageBody(message.getBody());
    }
  }
  
  /**
   * Obtain the id of job on the queue from the message body
   * 
   * @param messageBody The message body
   * @return The jobId
   */
  protected abstract I getJobIdFromMessageBody(String messageBody);
  
  /**
   * Create a message body from a jobId
   * 
   * @param jobId The jobId
   * @return The message body we wish to create for this jobId
   */
  protected abstract String createMessageBodyFromJobId(I jobId);

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#saveJob(com.echobox.jobqueue.
   * PersistentJobCommandOnQueue)
   */
  @Override
  public QueuedJobId<Q, I> saveJob(
      PersistentJobCommandOnQueue<C, Q, I> 
      persistentJobCommandOnQueue) {
    addToRepository(persistentJobCommandOnQueue.getId(), persistentJobCommandOnQueue);
    return persistentJobCommandOnQueue.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#getJob(com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public PersistentJobCommandOnQueue<C, Q, I> getJob(
      QueuedJobId<Q, I> queuedJobId) {
    return (PersistentJobCommandOnQueue<C, Q, I>) cacheService.tryGetCachedItem(
        getCacheKey(queuedJobId), getPersistentJobCommandOnQueueType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.echobox.jobqueue.JobCommandOnQueueRepository#deleteJob(com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public boolean deleteJob(QueuedJobId<Q, I> queuedJobId) {
    return cacheService.tryDeleteItemFromCache(getCacheKey(queuedJobId));
  }

  
  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#setJobSuccessfulCompletion(com.echobox.
   * jobqueue.QueuedJobId, java.lang.String, long)
   */
  @Override
  public boolean setJobSuccessfulCompletion(QueuedJobId<Q, I> queuedJobId,
      String consumerId, long completedUnixTime) {
    PersistentJobCommandOnQueue<C, Q, I> 
        existingJobOnQueue =  getJob(queuedJobId);

    if (existingJobOnQueue != null) {
      JobCommand<C> existingJob = existingJobOnQueue.getJob();
      existingJob.setSuccessfulCompletionUnixTime(completedUnixTime);
      addToRepository(queuedJobId, existingJobOnQueue);
      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#setJobUnsuccessfulCompletion(
   * com.echobox.jobqueue.QueuedJobId, java.lang.Exception, java.lang.String, long)
   */
  @Override
  public boolean setJobUnsuccessfulCompletion(QueuedJobId<Q, I> queuedJobId,
      Exception exception, String consumerId, long completedUnixTime) {
    PersistentJobCommandOnQueue<C, Q, I> 
        existingJobOnQueue = getJob(queuedJobId);
    if (existingJobOnQueue != null) {
      JobCommand<C> existingJob = existingJobOnQueue.getJob();
      existingJob.setUnsuccessfulCompletionUnixTime(completedUnixTime, exception);
      addToRepository(queuedJobId, existingJobOnQueue);

      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#updateHeartbeatTime(com.echobox.
   * jobqueue.QueuedJobId, java.lang.String, long)
   */
  @Override
  public boolean updateHeartbeatTime(QueuedJobId<Q, I> queuedJobId,
      String consumerId, long heartbeatUnixTime) {
    PersistentJobCommandOnQueue<C, Q, I> existingJobOnQueue = getJob(queuedJobId);
    if (existingJobOnQueue != null) {
      existingJobOnQueue.setLastHeartbeatTimeUnix(heartbeatUnixTime);
      addToRepository(queuedJobId, existingJobOnQueue);
      return true;
    } else {
      return false;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.PersistentJobCommandQueue#resetJob(com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public boolean resetJob(QueuedJobId<Q, I> queuedJobId) {
    
    PersistentJobCommandOnQueue<C, Q, I> existingJob = getJob(queuedJobId);
    if (existingJob != null) {
      existingJob.getJob().resetCompletionStatus();
      saveJob(existingJob);
      addJobToQueueWithId(existingJob.getJob(), queuedJobId);
    }
    
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteAllJobs(java.lang.Object)
   */
  @Override
  public boolean deleteAllJobs(Q queueType) {
    logger.warn("NOT DELETING ALL JOBS - Not yet implemented");
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#getAllConsumedJobs(java.lang.Object)
   */
  @Override
  public List<PersistentJobCommandOnQueue<C, Q, I>> 
      getAllConsumedJobs(
      Q queueType) {
    logger.warn("NOT OBTAINING CONSUMED JOBS - Not yet implemented");
    return new ArrayList<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteAllUnconsumedJobs(java.lang.Object)
   */
  @Override
  public boolean deleteAllUnconsumedJobs(Q queueType) {
    logger.warn("NOT DELETING UNCONSUMED JOBS - Not yet implemented");
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.echobox.jobqueue.JobCommandOnQueueRepository#reserveAllUnconsumedJobs(java.lang.Object,
   * java.lang.String)
   */
  @Override
  public boolean reserveAllUnconsumedJobs(Q queueType, String producerId) {
    logger.warn("NOT RESERVING UNCONSUMED JOBS - Not yet implemented");
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#getJobCount(java.lang.Object)
   */
  @Override
  public long getJobCount(Q queueType) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
