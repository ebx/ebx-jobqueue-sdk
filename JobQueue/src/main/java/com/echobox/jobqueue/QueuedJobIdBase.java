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

import java.io.Serializable;

/**
 * Convenient QueuedJobId base class for simple composite queued job ids, formed of a queueType
 * ( of type Q ) and a type of unique job identfier ( of type I)
 * 
 * @author Michael Lavelle
 * 
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 */
public class QueuedJobIdBase<Q extends Serializable, I extends Serializable> 
    implements QueuedJobId<Q, I> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private Q queueType;
  private I jobId;
  
  /**
   * The constructor
   * @param queueType The queue type
   * @param jobId The id of the job on this queue
   */
  public QueuedJobIdBase(Q queueType, I jobId) {
    this.queueType = queueType;
    this.jobId = jobId;
  }
  
  
  /* (non-Javadoc)
   * @see com.echobox.jobqueue.QueuedJobId#getQueueType()
   */
  @Override
  public Q getQueueType() {
    return queueType;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.QueuedJobId#getJobId()
   */
  @Override
  public I getJobId() {
    return jobId;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    QueuedJobIdBase<Q, I> other = (QueuedJobIdBase<Q, I>) obj;
    if (jobId == null) {
      if (other.jobId != null) {
        return false;
      }
    } else if (!jobId.equals(other.jobId)) {
      return false;
    }
    if (queueType != other.queueType) {
      return false;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
    result = prime * result + ((queueType == null) ? 0 : queueType.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "QueuedJobId [queueType=" + queueType + ", jobId=" + jobId + "]";
  }
}
