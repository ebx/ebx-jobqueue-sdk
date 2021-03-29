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

package com.echobox.jobqueue.demo.commands;

import com.echobox.jobqueue.demo.domain.DemoJobTypeEnum;
import com.echobox.jobqueue.demo.domain.bindings.DemoCommandExecutionContext;
import com.echobox.jobqueue.demo.domain.bindings.DemoWorkerJobCommandBase;
import com.echobox.jobqueue.events.JobCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo worker command
 * @author Michael Lavelle
 *
 */
public class DemoWorkerCommand extends DemoWorkerJobCommandBase {

  private static Logger logger = LoggerFactory.getLogger(DemoWorkerCommand.class);
  
  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private String demoMessage;
  
  /**
   * Initialise DemoWorkerCommand
   * @param jobCreationTimeUnix The creation time of this command
   * @param demoMessage A demo message to output
   */
  public DemoWorkerCommand(long jobCreationTimeUnix,  String demoMessage) {
    super(DemoJobTypeEnum.DEMO_WORKER_COMMAND, jobCreationTimeUnix);
    this.demoMessage = demoMessage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.echobox.jobqueue.commands.WorkerJobCommand#doWork(
   * com.echobox.jobqueue.context.JobCommandExecutionContext)
   */
  @Override
  protected void doWork(DemoCommandExecutionContext executionContext) throws Exception {
    
    logger.debug(getLogMessage(executionContext, JobCommandEvent.JOB_INFO, "Doing Some Work!"));
    logger.debug(getLogMessage(executionContext, JobCommandEvent.JOB_INFO, 
        "Simulating 20 seconds of work"));

    Thread.sleep(20000);
    logger.info(getLogMessage(executionContext, JobCommandEvent.JOB_INFO, demoMessage));
  }

  /* (non-Javadoc)
   * @see com.echobox.jobqueue.commands.WorkerJobCommand#getLogger()
   */
  @Override
  protected Logger getLogger() {
    return logger;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DemoWorkerCommand [demoMessage=" + demoMessage + "]";
  }
}
