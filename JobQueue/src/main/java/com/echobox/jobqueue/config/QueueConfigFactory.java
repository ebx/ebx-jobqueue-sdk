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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A default factory for QueueConfig instances
 *
 * @param <Q> The type of queue-type we use to distinguish between queues
 * 
 * @author Michael Lavelle
 */
public class QueueConfigFactory<Q> {

  private static Logger logger = LoggerFactory.getLogger(QueueConfigFactory.class);

  /**
   * The default heartbeat pulse period seconds
   */
  public static int DEFAULT_HEARTBEAT_PULSE_PERIOD_SECONDS = 30;

  /**
   * The default queue priority
   */
  public static int DEFAULT_QUEUE_PRIORITY = 5;

  private static QueueConfig DEFAULT_QUEUE_CONFIG =
      new QueueConfig().withHeartbeatPulsePeriodSecs(DEFAULT_HEARTBEAT_PULSE_PERIOD_SECONDS)
          .withPriority(DEFAULT_QUEUE_PRIORITY);

  private Map<Q, QueueConfig> queueConfigOverridesByQueueType;

  /**
   * Instantiates a new Queue config factory.
   */
  public QueueConfigFactory() {
    queueConfigOverridesByQueueType = new HashMap<Q, QueueConfig>();
  }

  /**
   * Add queue config override.
   *
   * @param queueType the queue type
   * @param queueConfig the queue config
   */
  public void addQueueConfigOverride(Q queueType, QueueConfig queueConfig) {
    queueConfigOverridesByQueueType.put(queueType, queueConfig);
  }

  /**
   * Obtain a dynamic queue config
   * @param queueType the queue type
   * @return The dynamic runtime queue config if any or null if not applicable
   * @throws Exception the exception
   */
  protected QueueConfig getRuntimeQueueConfig(Q queueType) throws Exception {
    // Return null by default
    return null;
  }

  /**
   * Gets queue config.
   *
   * @param queueType the queue type
   * @return The queueConfig for this queueType, or the DEFAULT_QUEUE_CONFIG if no explicit
   * config found
   */
  public QueueConfig getQueueConfig(Q queueType) {

    QueueConfig queueConfig = queueConfigOverridesByQueueType.get(queueType);
    if (queueConfig == null) {
      try {
        queueConfig = getRuntimeQueueConfig(queueType);
        if (queueConfig == null) {
          logger.trace("No explicit queue config or runtime config " + "for queueType:" + queueType
              + " returning default");
          return DEFAULT_QUEUE_CONFIG;
        }
      } catch (Exception e) {
        logger.error("Error obtainging runtime config " + "for queueType:" + queueType
            + " returning default", e);
        return DEFAULT_QUEUE_CONFIG;
      }
    }
    return queueConfig;
  }

  /**
   * Gets default consumers per core.
   *
   * @return The default number of consumers
   */
  public Double getDefaultConsumersPerCore() {
    // Null by default
    return null;
  }

  /**
   * Gets default queue types.
   *
   * @return default queue types
   */
  public Set<Q> getDefaultQueueTypes() {
    // Null by default
    return null;
  }
}
