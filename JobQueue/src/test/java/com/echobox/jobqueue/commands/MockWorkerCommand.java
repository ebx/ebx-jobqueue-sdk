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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Mock worker command.
 * 
 * @author Michael Lavelle
 */
public class MockWorkerCommand extends WorkerJobCommand<MockJobCommandExecutionContext> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;

  private static Logger logger = LoggerFactory.getLogger(MockWorkerCommand.class);

  private boolean throwException;

  private boolean sleep;

  private int numberOfExecutionAttempts;
  
  /**
   * Instantiates a new Mock worker command.
   *
   * @param jobType the job type
   * @param creationTimeUnix the job creation time unix
   */
  public MockWorkerCommand(JobType<?> jobType, long creationTimeUnix) {
    super(jobType, creationTimeUnix);
    this.throwException = false;
    this.sleep = false;
  }

  /**
   * Instantiates a new Mock worker command.
   *
   * @param jobCreationTimeUnix the job creation time unix
   * @param throwException the throw exception
   * @param sleep the sleep
   */
  public MockWorkerCommand(long jobCreationTimeUnix, boolean throwException, boolean sleep) {
    super(new MockJobType("mockWorker"), jobCreationTimeUnix);
    this.throwException = throwException;
    this.sleep = sleep;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.WorkerJobCommand#
   * doWork(com.echobox.jobqueue.context.JobCommandExecutionContext)
   */
  @Override
  protected void doWork(MockJobCommandExecutionContext executionContext) throws Exception {
    logger.debug("Doing some work");
    numberOfExecutionAttempts++;
    if (sleep) {
      Thread.sleep(5000);
    }
    if (throwException) {
      throw new RuntimeException("simulated exception");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.WorkerJobCommand#getLogger()
   */
  @Override
  protected Logger getLogger() {
    return logger;
  }

  /**
   * Gets number of execution attempts.
   *
   * @return numberOfExecutionAttempts number of execution attempts
   */
  public int getNumberOfExecutionAttempts() {
    return numberOfExecutionAttempts;
  }
}
