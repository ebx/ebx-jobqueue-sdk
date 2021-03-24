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

package com.echobox.jobqueue.demo.domain.bindings;

import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.context.JobCommandExecutionContextBase;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import com.echobox.shutdown.ShutdownMonitor;
import org.bson.types.ObjectId;

/**
 * Demo command execution context
 * @author Michael Lavelle
 */
public class DemoCommandExecutionContext extends 
    JobCommandExecutionContextBase<DemoCommandExecutionContext, DemoQueueType, ObjectId> {

  /**
   * Initialise DemoCommandExecutionContext
   * @param name The context name
   * @param jobCommandQueue The job command queue
   * @param applicationShutdownMonitor The application shutdown monitor
   * @param contextShutdownMonitor The context shutdown monitor
   */
  public DemoCommandExecutionContext(String name,
      PersistentQueuedJobCommandQueue<DemoCommandExecutionContext, DemoQueueType, 
      ObjectId> jobCommandQueue,
      ShutdownMonitor applicationShutdownMonitor, ShutdownMonitor contextShutdownMonitor) {
    super(name, jobCommandQueue, applicationShutdownMonitor, contextShutdownMonitor);
    this.setEnableLoggingForAllJobTypes(true);
  }
}
