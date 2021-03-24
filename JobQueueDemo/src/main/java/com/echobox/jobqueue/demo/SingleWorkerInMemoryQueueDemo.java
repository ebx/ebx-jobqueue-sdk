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

package com.echobox.jobqueue.demo;

import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.DequeuedJobCommand;
import com.echobox.jobqueue.demo.commands.DemoWorkerCommand;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import com.echobox.jobqueue.demo.domain.bindings.DemoCommandExecutionContext;
import com.echobox.jobqueue.demo.domain.bindings.DemoJobCommandQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueueConfigFactory;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobCommand;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobCommandQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobCommandQueueImpl;
import com.echobox.jobqueue.demo.impl.DemoInMemoryJobCommandQueueImpl;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.shutdown.ShutdownMonitor;
import com.echobox.shutdown.impl.SimpleShutdownMonitor;
import com.echobox.time.UnixTime;
import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Demo of JobQueue Producer/Consumer workflow
 * 
 * @author Michael Lavelle
 */
public class SingleWorkerInMemoryQueueDemo {

  private static Logger logger = LoggerFactory.getLogger(SingleWorkerInMemoryQueueDemo.class);

  /**
   * Run single worker in memory queue demo
   * @param args The program arguments
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
       
    // This is the main class you'll need to implement - the persistent queue itself
    DemoJobCommandQueue jobCommandQueue = new DemoInMemoryJobCommandQueueImpl();

    // Create a wrapper around the jobCommandQueue, which allows QueuedJobCommand instances
    // to be submitted for distributed execution
    DemoQueuedJobCommandQueue queuedJobCommandQueue = 
        new DemoQueuedJobCommandQueueImpl(jobCommandQueue, new DemoQueueConfigFactory());

    // Create a shutdown monitor we can pass to the execution context so we can listen
    // for shutdown requests
    ShutdownMonitor shutdownMonitor = new SimpleShutdownMonitor();
    
    // Create the producer execution context, passing in the command queue and the shutdown monitor
    DemoCommandExecutionContext producerExecutionContext =
        new DemoCommandExecutionContext("producer", queuedJobCommandQueue, shutdownMonitor, 
            shutdownMonitor);

    // Start a number of consumer threads - this would usually be on a different machine if this
    // wasn't a demo. These consumers are listening to the QUEUE_1 queue
    Thread demoConsumerThread1 = new Thread(new DemoConsumer("consumer1", queuedJobCommandQueue, 
        DemoQueueType.QUEUE_1,  shutdownMonitor));
    demoConsumerThread1.start();
    
    Thread demoConsumerThread2 = new Thread(new DemoConsumer("consumer2", queuedJobCommandQueue, 
        DemoQueueType.QUEUE_1,  shutdownMonitor));
    demoConsumerThread2.start();
    
    // Now that the consumers are listening, we now create the worker command we wish 
    // to execute on a consumer, we wrap the command inside a QueuedJobCommand adapter, 
    // and execute the adapter.   This will send the command to the queue and allow us to
    // monitor progress and wait for completion
    try {

      // Create the command we want to execute remotely.
      DemoWorkerCommand workerCommand = new DemoWorkerCommand(UnixTime.now(), "hello");

      // Wrap the command inside a QueuedJobCommand wrapper, passing in details of the queue
      // we want to send the command to - in this case its' the QUEUE_1 queue
      DemoQueuedJobCommand<DemoWorkerCommand> queuedCommand =
          new DemoQueuedJobCommand<>(queuedJobCommandQueue, DemoQueueType.QUEUE_1, workerCommand,
              UnixTime.now());
      
      // Execute the QueuedJobCommand, obtaining a handle to the future result, waiting up to 
      // 60 seconds.   Try changing this value to less than 20 to see how timeouts are handled
      Future<JobSuccess> futureProducerJobSuccess =
          queuedCommand.executeAsynchronously(producerExecutionContext, 60);

      // Wait for the remote job to complete
      JobSuccess jobSuccess = futureProducerJobSuccess.get();
      
      shutdownMonitor.setShutdownRequested(true);

      logger.debug(queuedCommand.getLogMessage(producerExecutionContext, 
          JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY, 
          "Producer reports job success:" + jobSuccess));
      
    } catch (ExecutionException e) {

      logException(producerExecutionContext,
          "Producer reported ExecutionException executing QueuedJobCommand", e);

    } catch (InterruptedException e) {
      logException(producerExecutionContext,
          "Producer reported InterruptedException executing QueuedJobCommand", e);
    }
  }

  private static void logException(DemoCommandExecutionContext context, String message,
      Exception ex) {
    logger.error(context + " : " + message, ex);

  }

  /**
   * Demo consumer runnable
   * 
   * @author Michael Lavelle
   */
  private static class DemoConsumer implements Runnable {

    private DemoQueuedJobCommandQueue queuedJobCommandQueue;
    private DemoQueueType queueType;
    private ShutdownMonitor shutdownMonitor;
    private String consumerName;

    DemoConsumer(String consumerName, DemoQueuedJobCommandQueue queuedJobCommandQueue,
        DemoQueueType queueType, ShutdownMonitor shutdownMonitor) {
      this.queuedJobCommandQueue = queuedJobCommandQueue;
      this.queueType = queueType;
      this.shutdownMonitor = shutdownMonitor;
      this.consumerName = consumerName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

      DemoCommandExecutionContext consumerExecutionContext =
          new DemoCommandExecutionContext(consumerName, queuedJobCommandQueue, shutdownMonitor, 
              shutdownMonitor);

      while (!shutdownMonitor.isShutdownRequested()) {

        try {

          Pair<QueuedJobId<DemoQueueType, ObjectId>, 
              DequeuedJobCommand<DemoCommandExecutionContext, 
              DemoQueueType, ObjectId>> nextJobFromQueue =
              queuedJobCommandQueue.getNextJob("myConsumer", queueType);

          if (nextJobFromQueue != null) {
            DequeuedJobCommand<DemoCommandExecutionContext, DemoQueueType, ObjectId> 
                dequeuedCommand =  nextJobFromQueue.getValue1();

            JobSuccess jobSuccess =
                dequeuedCommand.executeSynchronously(consumerExecutionContext, true);

            logger.debug(dequeuedCommand.getLogMessage(consumerExecutionContext, 
                JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY, 
                "Consumer reports job success:" + jobSuccess));
            
            Thread.sleep(1000);
          }

        } catch (ExecutionException e) {
          logException(consumerExecutionContext,
              "Producer reported ExecutionException executing DequeuedJobCommand", e);
        } catch (TimeoutException e) {
          logException(consumerExecutionContext,
              "Producer reported TimeoutException executing DequeuedJobCommand", e);
        } catch (InterruptedException e) {
          logException(consumerExecutionContext,
              "Producer reported InterruptedException executing DequeuedJobCommand", e);
        }
      }
    }
  }
}
