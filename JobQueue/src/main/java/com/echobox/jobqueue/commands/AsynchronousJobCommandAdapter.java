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

import com.echobox.jobqueue.commands.behaviour.JobCommandInterruptionStrategy;
import com.echobox.jobqueue.commands.status.FutureWithMaxWaitTimeout;
import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobProgressReport;
import com.echobox.jobqueue.status.JobSuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Adapts any job command ( synchronous or asynchronous ) so that it will be executed
 * asynchronously by default
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <J> The type of JobCommand we are adapting
 * 
 * @author Michael Lavelle
 */
public class AsynchronousJobCommandAdapter<C extends JobCommandExecutionContext<C, ?, ?>, J
    extends JobCommand<C>>
    extends SingleTaskCoordinatorJobCommand<C, J> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  private static Logger logger = LoggerFactory.getLogger(AsynchronousJobCommandAdapter.class);

  /**
   * The maximum number of seconds that
   * any asynchronous parts of the execution graph will wait for completion before
   * timing out - note that this does not apply to synchronous parts of the execution graph
   */
  private long maxAsyncExecutionCompletionWaitTimeoutSecs;

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isAsynchronousExecution()
   */
  @Override
  protected boolean isAsynchronousExecution() {
    return true;
  }

  /**
   * Instantiates a new Asynchronous job command adapter.
   *
   * @param jobCommand The job command we are adapting
   * @param maxAsyncExecutionCompletionWaitTimeoutSecs The maximum number of seconds that any
   *                                                   asynchronous parts of the execution graph
   *                                                   will wait for completion before timing out
   *                                                   - note that this does not apply to
   *                                                   synchronous parts of the execution graph
   */
  public AsynchronousJobCommandAdapter(J jobCommand,
      long maxAsyncExecutionCompletionWaitTimeoutSecs) {
    super(jobCommand, jobCommand.getJobCreationTimeUnix());
    this.maxAsyncExecutionCompletionWaitTimeoutSecs = maxAsyncExecutionCompletionWaitTimeoutSecs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#doExecute(
   * com.echobox.jobqueue.JobCommandExecutionContext,
   * long)
   */
  @Override
  protected Future<JobSuccess> doExecute(final C executionContext,
      final long maxAsyncExecutionCompletionWaitTimeoutSecs) throws Exception {

    final long mimumumMaxAsyncExecutionCompletionWaitTimeoutSecs = (long) (Math
        .min(maxAsyncExecutionCompletionWaitTimeoutSecs,
            this.maxAsyncExecutionCompletionWaitTimeoutSecs));

    if (jobCommand.isAsynchronousExecution()) {
      return jobCommand
          .executeOrRetry(executionContext, mimumumMaxAsyncExecutionCompletionWaitTimeoutSecs);
    } else {

      Future<JobSuccess> futureJobSuccess =
          executionContext.executeAsync(new Callable<JobSuccess>() {

            @Override
            public JobSuccess call() throws Exception {

              try {
                Future<JobSuccess> futureStatus = jobCommand
                    .executeOrRetry(executionContext, maxAsyncExecutionCompletionWaitTimeoutSecs);
                JobSuccess status = futureStatus
                    .get(mimumumMaxAsyncExecutionCompletionWaitTimeoutSecs, TimeUnit.SECONDS);
                return status;
              } catch (Exception e) {
                logger.error(jobCommand.getLogMessage(executionContext, JobCommandEvent.JOB_ERROR,
                    "Exception executing command in thread pool executor"), e);
                throw e;
              }
            }
          });
      return new FutureWithMaxWaitTimeout<JobSuccess>(futureJobSuccess,
          mimumumMaxAsyncExecutionCompletionWaitTimeoutSecs, TimeUnit.SECONDS);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#createInterruptStrategy()
   */
  @Override
  public JobCommandInterruptionStrategy createInterruptStrategy() {
    return jobCommand.createInterruptStrategy();
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.JobCommand#getProgressReport()
   */
  @Override
  public JobProgressReport getProgressReport() {
    return jobCommand.getProgressReport();
  }

}
