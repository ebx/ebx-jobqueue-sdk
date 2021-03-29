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

package com.echobox.jobqueue.context;

import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.PersistentQueuedJobCommandQueue;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.shutdown.ShutdownMonitor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Convenient base class for Execution contexts that a JobCommand is to run within
 *
 * @author Michael Lavelle
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 */
public class JobCommandExecutionContextBase<C extends JobCommandExecutionContextBase<C, Q, I>, 
    Q extends Serializable, I extends Serializable>
    implements JobCommandExecutionContext<C, Q, I> {

  /**
   * A name for this job command execution context
   */
  protected String name;

  private PersistentQueuedJobCommandQueue<C, Q, I> 
      jobCommandQueue;

  private ShutdownMonitor applicationShutdownMonitor;

  private ShutdownMonitor contextShutdownMonitor;

  private Set<Enum<?>> jobTypesWithLoggingEnabled;
  private boolean enableLoggingForAllJobTypes;

  /**
   * The Thread pool executor.
   */
  public ThreadPoolExecutor threadPoolExecutor;

  /**
   * Highest priority thread pool executor - used for updating heartbeats
   */
  public ThreadPoolExecutor highestPriorityThreadPoolExecutor;

  /**
   * Instantiates a new Ebx command execution context.
   *
   * @param name A name for this context
   * @param jobCommandQueue the job command queue
   * @param applicationShutdownMonitor The shutdown monitor for the application  ( note this is
   *                                   not the shutdown monitor for the execution context, but
   *                                   the monitor for the entire application)
   * @param contextShutdownMonitor The shutdown monitor for this execution context
   */
  public JobCommandExecutionContextBase(String name,
      PersistentQueuedJobCommandQueue<C, Q, I> 
      jobCommandQueue,
      ShutdownMonitor applicationShutdownMonitor, ShutdownMonitor contextShutdownMonitor) {
    this.name = name;
    this.threadPoolExecutor =
        (ThreadPoolExecutor) Executors.newCachedThreadPool(new DaemonFactory(name));
    this.highestPriorityThreadPoolExecutor =
        (ThreadPoolExecutor) Executors.newCachedThreadPool(new HigestPriorityDaemonFactory(name));
    this.jobCommandQueue = jobCommandQueue;
    this.contextShutdownMonitor = contextShutdownMonitor;
    this.jobTypesWithLoggingEnabled = new HashSet<Enum<?>>();
    this.applicationShutdownMonitor = applicationShutdownMonitor;
  }

  /**
   * Is enable logging for all job types boolean.
   *
   * @return The enableLoggingForAllJobTypes
   */
  public boolean isEnableLoggingForAllJobTypes() {
    return enableLoggingForAllJobTypes;
  }

  /**
   * Sets the enableLoggingForAllJobTypes
   * @param enableLoggingForAllJobTypes the enableLoggingForAllJobTypes to set
   */
  public void setEnableLoggingForAllJobTypes(boolean enableLoggingForAllJobTypes) {
    this.enableLoggingForAllJobTypes = enableLoggingForAllJobTypes;
  }

  /**
   * Add a job to the jobTypesWithLoggingEnabled set
   * @param jobCommandType The type of job command
   */
  public void enableLoggingForJobType(JobType<?> jobCommandType) {
    jobTypesWithLoggingEnabled.add(jobCommandType.getJobTypeEnum());
  }

  /**
   * Disable logging for job type.
   *
   * @param jobCommandType the job command type
   */
  public void disableLoggingForJobType(JobType<?> jobCommandType) {
    jobTypesWithLoggingEnabled.remove(jobCommandType.getJobTypeEnum());
  }

  /**
   * Checks if logging is enabled
   * @param jobCommandType The type of job command
   * @return Whether logging is enabled for the specified job type
   */
  public boolean isLoggingEnabledForJobType(JobType<?> jobCommandType) {
    return enableLoggingForAllJobTypes || jobTypesWithLoggingEnabled
        .contains(jobCommandType.getJobTypeEnum());
  }

  /**
   * Shutdown this execution context
   */
  public void shutdown() {
    threadPoolExecutor.shutdownNow();
    highestPriorityThreadPoolExecutor.shutdownNow();
  }

  /**
   * Gets the JobCommand queue
   * @return The JobCommand queue enabled for this context
   */
  public PersistentQueuedJobCommandQueue<C, Q, I> 
      getJobCommandQueue() {
    return jobCommandQueue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "JobCommandExecutionContext [name=" + name + "]";
  }

  /**
   * Gets name.
   *
   * @return The name of this execution context
   */
  public String getName() {
    return name;
  }

  /**
   * Submits a job to be executed Async
   * @param worker The worker that should be executed asynchronously
   * @return The future execution result
   */
  public Future<JobSuccess> executeAsync(Callable<JobSuccess> worker) {
    return threadPoolExecutor.submit(worker);
  }

  /**
   * Execute async future.
   *
   * @param worker the worker
   * @return The future result
   */
  public Future<?> executeAsync(Runnable worker) {
    return threadPoolExecutor.submit(worker);
  }

  /**
   * Execute highest priority async future.
   *
   * @param worker the worker
   * @return The future result
   */
  public Future<?> executeHighestPriorityAsync(Runnable worker) {
    return highestPriorityThreadPoolExecutor.submit(worker);
  }

  /**
   * Execute highest priority async future.
   *
   * @param worker the worker
   * @return The future execution result
   */
  public Future<JobSuccess> executeHighestPriorityAsync(Callable<JobSuccess> worker) {
    return highestPriorityThreadPoolExecutor.submit(worker);
  }

  /**
   * The type Daemon factory.
   * @author N /A
   */
  public static class DaemonFactory implements ThreadFactory {

    private String name;

    /**
     * Instantiates a new Daemon factory.
     *
     * @param name the name
     */
    public DaemonFactory(String name) {
      this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable);
      thread.setName(name);
      thread.setDaemon(true);
      return thread;
    }
  }

  /**
   * The type Higest priority daemon factory.
   * @author N /A
   */
  public static class HigestPriorityDaemonFactory implements ThreadFactory {

    private String name;

    /**
     * Instantiates a new Higest priority daemon factory.
     *
     * @param name the name
     */
    public HigestPriorityDaemonFactory(String name) {
      this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable);
      thread.setName(name);
      thread.setDaemon(true);
      thread.setPriority(Thread.MAX_PRIORITY);
      return thread;
    }
  }

  /**
   * Gets context shutdown monitor.
   *
   * @return The shutdown monitor for this context
   */
  public ShutdownMonitor getContextShutdownMonitor() {
    return contextShutdownMonitor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext#isShutdownRequested()
   */
  @Override
  public boolean isShutdownRequested() {
    return contextShutdownMonitor.isShutdownRequested() || applicationShutdownMonitor
        .isShutdownRequested();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.context.JobCommandExecutionContext#isApplicationShutdownRequested()
   */
  @Override
  public boolean isApplicationShutdownRequested() {
    return applicationShutdownMonitor.isShutdownRequested();
  }
}
