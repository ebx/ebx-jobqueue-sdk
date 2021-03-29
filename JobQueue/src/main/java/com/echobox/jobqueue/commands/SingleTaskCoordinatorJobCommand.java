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

import com.echobox.jobqueue.context.JobCommandExecutionContext;
import com.echobox.jobqueue.status.JobStatus;

/**
 * SingleTaskCoordinatorJobCommand can execute either synchronously or asynchronously depending on
 * on the nature of the single JobCommand they are coordinating, and sets its completion status
 * according the the completion status of the delegate
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <J> The type of JobCommand which is being coordinated
 * 
 * @author Michael Lavelle
 */
public abstract class SingleTaskCoordinatorJobCommand<C extends JobCommandExecutionContext<C, ?,
    ?>, J extends JobCommand<C>>
    extends CoordinatorJobCommand<C> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  /**
   * The job command that we are coordinating
   */
  protected final J jobCommand;

  /**
   * Instantiates a new Single task coordinator job command.
   *
   * @param jobCommand the job command
   * @param jobCreationTimeUnix the job creation time unix
   */
  public SingleTaskCoordinatorJobCommand(J jobCommand, long jobCreationTimeUnix) {
    super(jobCommand.getJobType(), jobCreationTimeUnix);
    this.jobCommand = jobCommand;
    this.jobCommand.addCompletionListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.CoordinatorJobCommand#getDelegatedCompletionStatus()
   */
  @Override
  protected JobStatus getDelegatedStatus() {
    return jobCommand.determineJobStatus();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isAsynchronousExecution()
   */
  @Override
  protected boolean isAsynchronousExecution() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#isCompletionStatusLoggingEnabled()
   */
  @Override
  public boolean isCompletionStatusLoggingEnabled(C executionContext) {
    return jobCommand.isCompletionStatusLoggingEnabled(executionContext);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.JobCommand#cleanUp(com.echobox.jobqueue.
   * JobCommandExecutionContext)
   */
  @Override
  public void cleanUp(C executionContext) throws Exception {
    jobCommand.cleanUp(executionContext);
  }
}
