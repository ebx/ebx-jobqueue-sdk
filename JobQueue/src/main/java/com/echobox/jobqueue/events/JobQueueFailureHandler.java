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

package com.echobox.jobqueue.events;

import com.echobox.jobqueue.commands.DequeuedJobCommand;
import com.echobox.jobqueue.context.JobCommandExecutionContext;

import java.io.Serializable;

/**
 * Callback interface for handling heartbeat update and Application shutdown failures 
 * on DequeuedJobCommands
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public interface JobQueueFailureHandler<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable> {

  /**
   * Callback method to be implemented in order to handle heartbeat failures
   * for DequeuedJobCommand instances 
   *
   * @param dequeuedCommand The de-queued command for which the heartbeat update failed
   */
  void onHeartbeatFailure(DequeuedJobCommand<C, Q, I> dequeuedCommand);

  /**
   * Callback methods to be implemented in order to handle application shutdown
   */
  void onApplicationShutDown();
}
