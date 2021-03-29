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

import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.context.JobCommandExecutionContext;

import java.io.Serializable;

/**
 * Convenient base class for PersistentJobCommandOnQueue domain objects.  Applications are free
 * to create their own implementations if this class is not sufficient for their needs, for example
 * if persistence annotations need to be added.
 * 
 * @author Michael Lavelle
 * 
 * @param <C> The type of JobCommandExecutionContext in which the wrapped command executes
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 */
public class PersistentJobCommandOnQueueBase<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable> 
    implements PersistentJobCommandOnQueue<C, Q, I>, Serializable {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private JobCommand<C> jobCommand;
  private QueuedJobId<Q, I> queuedJobId;
  private Long lastHeartbeatUnixTime;
  private Long consumptionUnixTime;
  private String consumerId;
  
  /**
   * The constructor
   * @param queuedJobId The queued job id
   * @param jobCommand The job command we are persisting
   */
  public PersistentJobCommandOnQueueBase(QueuedJobId<Q, I> queuedJobId, 
      JobCommand<C> jobCommand) {
    this.jobCommand = jobCommand;
    this.queuedJobId = queuedJobId;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#getJob()
   */
  @Override
  public JobCommand<C> getJob() {
    return jobCommand;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#getId()
   */
  @Override
  public QueuedJobId<Q, I> getId() {
    return queuedJobId;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#getLastHeartbeatTimeUnix()
   */
  @Override
  public Long getLastHeartbeatTimeUnix() {
    return lastHeartbeatUnixTime;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#getConsumptionTimeUnix()
   */
  @Override
  public Long getConsumptionTimeUnix() {
    return consumptionUnixTime;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#getConsumerId()
   */
  @Override
  public String getConsumerId() {
    return consumerId;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#setConsumerId(java.lang.String)
   */
  @Override
  public void setConsumerId(String consumerId) {
    this.consumerId = consumerId;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.PersistentJobOnQueue#setConsumptionTimeUnix(java.lang.Long)
   */
  @Override
  public void setConsumptionTimeUnix(Long consumptionTimeUnix) {
    this.consumptionUnixTime = consumptionTimeUnix;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.PersistentJobOnQueue#setLastHeartbeatTimeUnix(java.lang.Long)
   */
  @Override
  public void setLastHeartbeatTimeUnix(Long lastHeartbeatTimeUnix) {
    this.lastHeartbeatUnixTime = lastHeartbeatTimeUnix;
  }
}
