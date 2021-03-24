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
 * Composite id for each job on a JobQueue - consisting of a specified queue type ( of type Q )
 * and jobId ( of type I )
 *
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public interface QueuedJobId<Q extends Serializable, I extends Serializable> extends Serializable {

  /**
   * Gets queue type.
   *
   * @return The queue type
   */
  Q getQueueType();

  /**
   * Gets job id.
   *
   * @return The job id
   */
  I getJobId();
}
