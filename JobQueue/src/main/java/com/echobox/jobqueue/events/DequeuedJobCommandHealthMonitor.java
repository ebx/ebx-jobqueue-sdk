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
import com.echobox.time.UnixTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Runnable which monitors the health (heartbeat and application status) of a job command that
 * has been queued and subsequently dequeued, via a DequeuedJobCommand instance
 *
 * @param <C> The type of JobCommandExecutionContext in which we execute commands
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 * 
 * @author Michael Lavelle
 */
public class DequeuedJobCommandHealthMonitor<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable> implements Runnable {

  /**
   * A logging instance for this class
   */
  private static Logger logger = LoggerFactory.getLogger(DequeuedJobCommandHealthMonitor.class);

  private DequeuedJobCommand<C, Q, I> dequeuedJobCommand;
  private boolean stopped;
  private long pulsePeriodSeconds;
  private JobQueueFailureHandler<C, Q, I> failureHandler;
  private JobCommandExecutionContext<C, Q, I> executionContext;
  private int heartbeatFailureCount;

  /**
   * Instantiates a new Dequeued job command health monitor.
   *
   * @param executionContext the execution context
   * @param dequeuedJobCommand the dequeued job command
   * @param pulsePeriodSeconds the pulse period seconds
   * @param heartbeatFailureHandler the heartbeat failure handler
   */
  public DequeuedJobCommandHealthMonitor(C executionContext,
      DequeuedJobCommand<C, Q, I> dequeuedJobCommand, long pulsePeriodSeconds,
      JobQueueFailureHandler<C, Q, I> heartbeatFailureHandler) {
    this.dequeuedJobCommand = dequeuedJobCommand;
    this.stopped = false;
    this.pulsePeriodSeconds = pulsePeriodSeconds;
    this.failureHandler = heartbeatFailureHandler;
    this.executionContext = executionContext;
    this.heartbeatFailureCount = 0;
  }

  /**
   * Instructs the heartbeats to stop
   */
  public void stopPulse() {
    this.stopped = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    while (!stopped && !executionContext.isApplicationShutdownRequested()) {
      try {
        Thread.sleep(pulsePeriodSeconds * 1000);
      } catch (InterruptedException e) {
        stopped = true;
      }
      if (!stopped) {
        if (!executionContext.isShutdownRequested()) {
          boolean heartbeatUpdated = dequeuedJobCommand.updateHeartbeatTime(UnixTime.now());
          if (failureHandler != null && !heartbeatUpdated) {
            heartbeatFailureCount++;
            if (heartbeatFailureCount > 1) {
              failureHandler.onHeartbeatFailure(dequeuedJobCommand);
            } else {
              logger.warn(
                  "First Heartbeat update failed on job with id:" + dequeuedJobCommand.getId()
                      + " - not calling failure handler unless this happens again");
            }
          }

        }
      }
    }

    if (executionContext.isApplicationShutdownRequested()) {
      failureHandler.onApplicationShutDown();
    }
  }
}
