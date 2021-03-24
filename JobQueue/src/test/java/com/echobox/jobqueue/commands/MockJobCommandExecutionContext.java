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

package com.echobox.jobqueue.commands;

import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.status.JobSuccess;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The type Mock job command execution context.
 * 
 * @author Michael Lavelle
 */
public class MockJobCommandExecutionContext
    implements JobCommandExecutionContext<MockJobCommandExecutionContext, Serializable, 
    Serializable> {

  private ExecutorService executorService;

  /**
   * Instantiates a new Mock job command execution context.
   */
  public MockJobCommandExecutionContext() {
    this.executorService = Executors.newCachedThreadPool();
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext
   * #executeAsync(java.util.concurrent.Callable)
   */
  @Override
  public Future<JobSuccess> executeAsync(Callable<JobSuccess> callable) {
    return executorService.submit(callable);
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext#
   * isLoggingEnabledForJobType(com.echobox.jobqueue.JobType)
   */
  @Override
  public boolean isLoggingEnabledForJobType(JobType<?> jobType) {
    return true;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext#getJobCommandQueue()
   */
  @Override
  public PersistentQueuedJobCommandQueue<MockJobCommandExecutionContext, Serializable, 
      Serializable> 
      getJobCommandQueue() {
    return null;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext
   * #setEnableLoggingForAllJobTypes(boolean)
   */
  @Override
  public void setEnableLoggingForAllJobTypes(boolean enableLogging) {
    // No-op
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext
   * #enableLoggingForJobType(com.echobox.jobqueue.JobType)
   */
  @Override
  public void enableLoggingForJobType(JobType<?> jobType) {
    // No-op
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext#isShutdownRequested()
   */
  @Override
  public boolean isShutdownRequested() {
    return false;
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext#isApplicationShutdownRequested()
   */
  @Override
  public boolean isApplicationShutdownRequested() {
    return false;
  }
}
