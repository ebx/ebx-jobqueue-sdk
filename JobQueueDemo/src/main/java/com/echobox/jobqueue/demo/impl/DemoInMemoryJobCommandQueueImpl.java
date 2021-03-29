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

package com.echobox.jobqueue.demo.impl;

import com.echobox.jobqueue.PersistentJobCommandOnQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import com.echobox.jobqueue.demo.domain.bindings.DemoCommandExecutionContext;
import com.echobox.jobqueue.demo.domain.bindings.DemoJobCommandQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoPersistentJobCommandOnQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobId;
import com.echobox.time.UnixTime;
import org.bson.types.ObjectId;
import org.javatuples.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple in-memory implementation of DemoJobCommandQueue.  This is only used for demo purposes
 * as it cannot be used within a distributed environment due to the in-memory persistence and 
 * queueing.
 * 
 * To productionise this class, replace the queue and repository implementations with distributed
 * versions, and implement the not-yet-implemented methods.
 * 
 * @author Michael Lavelle
 */
public class DemoInMemoryJobCommandQueueImpl implements DemoJobCommandQueue {

  private Map<DemoQueueType, Queue<ObjectId>> queuesByQueueType;
  
  private Map<QueuedJobId<DemoQueueType, ObjectId>, 
      PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId>> repository;

  /**
   * Default constructor
   */
  public DemoInMemoryJobCommandQueueImpl() {
    this.queuesByQueueType = new HashMap<>();
    this.queuesByQueueType.put(DemoQueueType.QUEUE_1, new LinkedBlockingQueue<>());
    this.queuesByQueueType.put(DemoQueueType.QUEUE_2, new LinkedBlockingQueue<>());
    this.repository = new HashMap<>();
  }
  
  private Queue<ObjectId> getQueue(DemoQueueType queueType) {
    Queue<ObjectId> queue = queuesByQueueType.get(queueType);
    if (queue == null) {
      throw new IllegalArgumentException("No queue found for queueType:" + queueType);
    }
    return queue;
  }

  private void addToRepository(QueuedJobId<DemoQueueType, ObjectId> key,
      PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, 
      ObjectId> jobCommand) {

    try {

      PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
          clonedCommand = serializeAndDeserializeCommand(jobCommand);

      repository.put(key, clonedCommand);

    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Unable to add to repository", e);
    }
  }

  private PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
      serializeAndDeserializeCommand(PersistentJobCommandOnQueue<DemoCommandExecutionContext, 
          DemoQueueType, ObjectId> command) throws IOException, ClassNotFoundException {

    ByteArrayOutputStream outputStream = null;
    PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
        returnCommand = null;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
        oos.writeObject(command);
      }
      outputStream = baos;
    }
    try (ByteArrayInputStream bois =
        new ByteArrayInputStream(outputStream.toByteArray())) {
      try (ObjectInputStream ois = new ObjectInputStream(bois)) {
        PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
            deserialized = 
            (PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId>) 
            ois.readObject();
        returnCommand = deserialized;
      }
    }
    return returnCommand;

  }


  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.PersistentJobCommandQueue#resetJob(com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public boolean resetJob(QueuedJobId<DemoQueueType, ObjectId> queuedJobId) {

    PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> existingJob =
        getJob(queuedJobId);
    if (existingJob != null) {

      existingJob.getJob().resetCompletionStatus();
      deleteJob(queuedJobId);
      addJobToQueue(existingJob.getJob(), queuedJobId.getQueueType());
      return true;

    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#addJobToQueue(com.echobox.jobqueue.Job, java.lang.Object)
   */
  @Override
  public QueuedJobId<DemoQueueType, ObjectId> addJobToQueue(
      JobCommand<DemoCommandExecutionContext> job, DemoQueueType queueType) {

    ObjectId generatedJobId = ObjectId.get();

    QueuedJobId<DemoQueueType, ObjectId> queuedJobId =
        new DemoQueuedJobId(queueType, generatedJobId);

    DemoPersistentJobCommandOnQueue command = new DemoPersistentJobCommandOnQueue(queuedJobId,
        job);

    addToRepository(queuedJobId, command);

    getQueue(queueType).add(generatedJobId);

    return queuedJobId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#addJobToQueueWithId(com.echobox.jobqueue.Job,
   * com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public QueuedJobId<DemoQueueType, ObjectId> addJobToQueueWithId(
      JobCommand<DemoCommandExecutionContext> job,
      QueuedJobId<DemoQueueType, ObjectId> queuedJobId) {

    DemoPersistentJobCommandOnQueue command = new DemoPersistentJobCommandOnQueue(queuedJobId,
        job);

    addToRepository(queuedJobId, command);

    getQueue(queuedJobId.getQueueType()).add(queuedJobId.getJobId());

    return queuedJobId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#getNextJob(java.lang.String, java.lang.Object)
   */
  @Override
  public Pair<QueuedJobId<DemoQueueType, ObjectId>, 
      PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId>> getNextJob(
      String consumerId, DemoQueueType queueType) {
    ObjectId jobId = getQueue(queueType).poll();
    
    QueuedJobId<DemoQueueType, ObjectId> queuedJobId = new DemoQueuedJobId(queueType, jobId);
    
    PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
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

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobQueue#getJobCount(java.lang.Object)
   */
  @Override
  public long getJobCount(DemoQueueType queueType) {
    return getQueue(queueType).size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#saveJob(com.echobox.jobqueue.
   * PersistentJobCommandOnQueue)
   */
  @Override
  public QueuedJobId<DemoQueueType, ObjectId> saveJob(
      PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
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
  public PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> getJob(
      QueuedJobId<DemoQueueType, ObjectId> queuedJobId) {
    return repository.get(queuedJobId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.echobox.jobqueue.JobCommandOnQueueRepository#deleteJob(com.echobox.jobqueue.QueuedJobId)
   */
  @Override
  public boolean deleteJob(QueuedJobId<DemoQueueType, ObjectId> queuedJobId) {
    repository.remove(queuedJobId);
    return repository.containsKey(queuedJobId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteAllJobs(java.lang.Object)
   */
  @Override
  public boolean deleteAllJobs(DemoQueueType queueType) {
    repository.clear();
    return repository.isEmpty();
  }

  
  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#setJobSuccessfulCompletion(com.echobox.
   * jobqueue.QueuedJobId, java.lang.String, long)
   */
  @Override
  public boolean setJobSuccessfulCompletion(QueuedJobId<DemoQueueType, ObjectId> queuedJobId,
      String consumerId, long completedUnixTime) {
    PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
        existingJobOnQueue =  getJob(queuedJobId);

    if (existingJobOnQueue != null) {
      if (consumerId != null && consumerId.equals(existingJobOnQueue.getConsumerId())) {
        JobCommand<DemoCommandExecutionContext> existingJob = existingJobOnQueue.getJob();
        existingJob.setSuccessfulCompletionUnixTime(completedUnixTime);
        return true;
      } else {
        return false;
      }
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
  public boolean setJobUnsuccessfulCompletion(QueuedJobId<DemoQueueType, ObjectId> queuedJobId,
      Exception exception, String consumerId, long completedUnixTime) {
    PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
        existingJobOnQueue = getJob(queuedJobId);
    if (existingJobOnQueue != null) {
      if (consumerId != null && consumerId.equals(existingJobOnQueue.getConsumerId())) {
        JobCommand<DemoCommandExecutionContext> existingJob = existingJobOnQueue.getJob();
        existingJob.setUnsuccessfulCompletionUnixTime(completedUnixTime, exception);
        return true;
      } else {
        return false;
      }
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
  public boolean updateHeartbeatTime(QueuedJobId<DemoQueueType, ObjectId> queuedJobId,
      String consumerId, long heartbeatUnixTime) {
    PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
        existingJobOnQueue = getJob(queuedJobId);
    if (existingJobOnQueue != null) {
      if (consumerId != null && consumerId.equals(existingJobOnQueue.getConsumerId())) {
        existingJobOnQueue.setLastHeartbeatTimeUnix(heartbeatUnixTime);
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#getAllConsumedJobs(java.lang.Object)
   */
  @Override
  public List<PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId>> 
      getAllConsumedJobs(
      DemoQueueType queueType) {
    throw new UnsupportedOperationException("Not implemented for the purposes of this demo");

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.JobCommandOnQueueRepository#deleteAllUnconsumedJobs(java.lang.Object)
   */
  @Override
  public boolean deleteAllUnconsumedJobs(DemoQueueType queueType) {
    throw new UnsupportedOperationException("Not implemented for the purposes of this demo");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.echobox.jobqueue.JobCommandOnQueueRepository#reserveAllUnconsumedJobs(java.lang.Object,
   * java.lang.String)
   */
  @Override
  public boolean reserveAllUnconsumedJobs(DemoQueueType queueType, String producerId) {
    throw new UnsupportedOperationException("Not implemented for the purposes of this demo");
  }

}
