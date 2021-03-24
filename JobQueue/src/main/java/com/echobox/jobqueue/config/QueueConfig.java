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

package com.echobox.jobqueue.config;

import org.mongodb.morphia.annotations.Entity;

/**
 * Encapsulates the attributes we can configure for a particular queue type (eg. the priority
 * and heartbeat pulse interval)
 * 
 * @author Michael Lavelle
 */
@Entity(noClassnameStored = true)
public class QueueConfig {

  /**
   * While a consumer is executing jobs consumed from a QueueType, it should send a heartbeat
   * pulse signal to the job on the queue to record the fact that the consumer is still
   * alive.  This is the period of that pulse in seconds.
   */
  private int heartbeatPulsePeriodSecs;

  private int priority;

  /**
   * With heartbeat pulse period secs queue config.
   *
   * @param heartbeatPulsePeriodSecs While a consumer is executing jobs consumed from a
   *                                 QueueType,  it should send a heartbeat pulse signal to the
   *                                 job on the queue to record the fact that  the consumer is
   *                                 still  alive.  This is the period of that pulse in seconds.
   * @return The updated queue config
   */
  public QueueConfig withHeartbeatPulsePeriodSecs(int heartbeatPulsePeriodSecs) {
    this.heartbeatPulsePeriodSecs = heartbeatPulsePeriodSecs;
    return this;
  }

  /**
   * With priority queue config.
   *
   * @param priority The queue priority
   * @return The updated queue config
   */
  public QueueConfig withPriority(int priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Gets heartbeat pulse period secs.
   *
   * @return The heartbeatPulsePeriodSecs While a consumer is executing jobs consumed from a
   * QueueType, it should send a heartbeat pulse signal to the job on the queue to record the
   * fact  that the consumer is still  alive.  This is the period of that pulse in seconds.
   */
  public int getHeartbeatPulsePeriodSecs() {
    return heartbeatPulsePeriodSecs;
  }

  /**
   * Gets priority.
   *
   * @return The priority
   */
  public int getPriority() {
    return priority;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "QueueConfig [heartbeatPulsePeriodSecs=" + heartbeatPulsePeriodSecs + ", priority="
        + priority + "]";
  }
}
