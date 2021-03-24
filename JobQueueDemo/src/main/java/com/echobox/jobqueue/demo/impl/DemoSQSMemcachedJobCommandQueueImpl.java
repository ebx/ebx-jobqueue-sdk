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

import com.amazonaws.services.sqs.AmazonSQS;
import com.echobox.cache.impl.MemcachedCacheService;
import com.echobox.jobqueue.PersistentJobCommandOnQueue;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import com.echobox.jobqueue.demo.domain.bindings.DemoCommandExecutionContext;
import com.echobox.jobqueue.demo.domain.bindings.DemoJobCommandQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoPersistentJobCommandOnQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobId;
import com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase;
import com.google.gson.reflect.TypeToken;
import org.bson.types.ObjectId;

import java.util.Map;

/**
 * Demo SQS Memcached job command queue implementation
 * @author Michael Lavelle
 *
 */
public class DemoSQSMemcachedJobCommandQueueImpl extends SQSCacheJobCommandQueueBase<
    DemoCommandExecutionContext, DemoQueueType, ObjectId> implements DemoJobCommandQueue {
 
  /**
   * For this demo implementation, we only store persistent jobs on the queue for a maximum of 
   * 1 hour before they expire and are in effect deleted.  
   * After consumption, the expiry time of the jobs will be extended on each heartbeat.
   */
  private static final int MAXIMUM_NUMBER_OF_SECONDS_PERMITTED_ON_QUEUE = 3600;
  
  private Map<DemoQueueType, String> queueUrlsByQueueType;
 
  /**
   * Initialise DemoSQSMemcachedJobCommandQueueImpl
   * @param sqs The SQS Instance
   * @param queueUrlsByQueueType 
   */
  public DemoSQSMemcachedJobCommandQueueImpl(AmazonSQS sqs, 
      Map<DemoQueueType, String> queueUrlsByQueueType) {
    super(sqs, MemcachedCacheService.getInstance(), 
        "DemoSQSMemcachedJobCommandQueueImpl", MAXIMUM_NUMBER_OF_SECONDS_PERMITTED_ON_QUEUE);
    this.queueUrlsByQueueType = queueUrlsByQueueType;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase#
   * createPersistentJobCommandOnQueue(com.echobox.jobqueue.QueuedJobId, 
   * com.echobox.jobqueue.commands.JobCommand)
   */
  @Override
  protected PersistentJobCommandOnQueue<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
      createPersistentJobCommandOnQueue(
      QueuedJobId<DemoQueueType, ObjectId> queuedJobId, 
      JobCommand<DemoCommandExecutionContext> jobCommand) {
    return new DemoPersistentJobCommandOnQueue(queuedJobId, jobCommand);
  }
  
  @Override
  protected TypeToken<?> getPersistentJobCommandOnQueueType() {
    return null;
  }
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase#createQueuedJobId(
   * java.io.Serializable, java.io.Serializable)
   */
  @Override
  protected QueuedJobId<DemoQueueType, ObjectId> createQueuedJobId(DemoQueueType demoQueueType,
      ObjectId objectId) {
    return new DemoQueuedJobId(demoQueueType, objectId);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase#generateNewJobId()
   */
  @Override
  protected ObjectId generateNewJobId() {
    return ObjectId.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase
   * #getJobIdFromMessageBody(java.lang.String)
   */
  @Override
  protected ObjectId getJobIdFromMessageBody(String messageBody) {
    return new ObjectId(messageBody);
  }
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase
   * #createMessageBodyFromJobId(java.io.Serializable)
   */
  @Override
  protected String createMessageBodyFromJobId(ObjectId objectId) {
    return objectId.toString();
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase#
   * getPersistentJobCommandOnQueueClass()
   */
  protected Class<? extends PersistentJobCommandOnQueue<DemoCommandExecutionContext,
      DemoQueueType, ObjectId>> getPersistentJobCommandOnQueueClass() {
    return DemoPersistentJobCommandOnQueue.class;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.sqscache.SQSCacheJobCommandQueueBase#getSQSQueueUrl(
   * java.io.Serializable)
   */
  @Override
  protected String getSQSQueueUrl(DemoQueueType queueType) {
    
    String queueUrl = queueUrlsByQueueType.get(queueType);
    if (queueUrl == null) {
      throw new IllegalStateException("No queue url defined for queue type:" + queueType);
    }
    return queueUrl;
  }
}
