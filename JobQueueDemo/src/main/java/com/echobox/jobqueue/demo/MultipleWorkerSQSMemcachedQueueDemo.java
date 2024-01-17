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

import com.echobox.cache.impl.MemcachedCacheService;
import com.echobox.jobqueue.QueuedJobId;
import com.echobox.jobqueue.commands.DequeuedJobCommand;
import com.echobox.jobqueue.demo.commands.DemoCoordinatorCommand;
import com.echobox.jobqueue.demo.domain.DemoQueueType;
import com.echobox.jobqueue.demo.domain.bindings.DemoCommandExecutionContext;
import com.echobox.jobqueue.demo.domain.bindings.DemoJobCommandQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueueConfigFactory;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobCommandQueue;
import com.echobox.jobqueue.demo.domain.bindings.DemoQueuedJobCommandQueueImpl;
import com.echobox.jobqueue.demo.impl.DemoSQSMemcachedJobCommandQueueImpl;
import com.echobox.jobqueue.events.JobCommandEvent;
import com.echobox.jobqueue.status.JobSuccess;
import com.echobox.shutdown.ShutdownMonitor;
import com.echobox.shutdown.impl.SimpleShutdownMonitor;
import com.echobox.time.UnixTime;
import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Demo of JobQueue Producer/Consumer workflow
 * 
 * @author Michael Lavelle
 */
public class MultipleWorkerSQSMemcachedQueueDemo {

  private static final Logger logger =
      LoggerFactory.getLogger(MultipleWorkerSQSMemcachedQueueDemo.class);
  
  private static SqsClient sqsClient;
  
  private static Map<DemoQueueType, String> queueUrlsByQueueType;

  /**
   * Run multiple worker SQS Memcached queue demo
   * @param args The program arguments
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
       
    // Create a shutdown monitor we can pass to the execution context so we can listen
    // for shutdown requests
    ShutdownMonitor shutdownMonitor = new SimpleShutdownMonitor();
   
    initialiseCacheAndSQS("src/main/resources/app.properties", shutdownMonitor);
         
    // This is the main class you'll need to implement - the persistent queue itself
    // Here we provide a demo SQS-Memcached implementation 
  
    DemoJobCommandQueue jobCommandQueue =
        new DemoSQSMemcachedJobCommandQueueImpl(sqsClient, queueUrlsByQueueType);

    // Create a QueuedJobCommandQueue wrapper around the jobCommandQueue which allows 
    // QueuedJobCommand instances to be submitted for distributed execution
    DemoQueuedJobCommandQueue queuedJobCommandQueue = 
        new DemoQueuedJobCommandQueueImpl(jobCommandQueue, new DemoQueueConfigFactory());

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
    
    // Now that the consumers are listening, we now create the coordinator command we wish 
    // to execute - this coordinator creates multiple worker commands, each of which is wrapped
    // inside a QueuedJobCommand and sent to the queue for distributed processing by the 
    // consumers
    try {

      // Create the command we want to execute so as to send multiple worker commands to the queue.
      // - one for each message we pass in here
      DemoCoordinatorCommand coordinatorCommand = 
          new DemoCoordinatorCommand(queuedJobCommandQueue, UnixTime.now(), "hello", "world");

      // Execute the coordinatorCommand, obtaining a handle to the future result, waiting up to 
      // 60 seconds.   Try changing this value to less than 20 to see how timeouts are handled
      Future<JobSuccess> futureProducerCoordinatorJobSuccess =
          coordinatorCommand.executeAsynchronously(producerExecutionContext, 60);

      // Wait for the all the remote jobs to complete
      JobSuccess jobSuccess = futureProducerCoordinatorJobSuccess.get();
      
      logger.debug(coordinatorCommand.getLogMessage(producerExecutionContext, 
          JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY, 
          "Producer reports job success:" + jobSuccess));

      shutdownMonitor.setShutdownRequested(true);
      
      shutdownCacheConnections();

    } catch (ExecutionException e) {

      logException(producerExecutionContext,
          "Producer reported ExecutionException executing Coordinator command", e);

    } catch (InterruptedException e) {
      logException(producerExecutionContext,
          "Producer reported InterruptedException executing Coordinator command", e);
    }
  }
  
  /**
   * Initialise the cache and sqs from the properties file
   *
   * @param fileName the properties file name
   * @param shutdownMonitor The shutdown monitor
   * @throws Exception the exception
   */
  public static void initialiseCacheAndSQS(String fileName, 
      ShutdownMonitor shutdownMonitor) throws Exception {

    if (MemcachedCacheService.getInstance().isCacheAvailable()) {
      MemcachedCacheService.shutdown();
    }

    // Determine where the file should be located
    String dbCredsLocation = System.getProperty("user.dir") + "/" + fileName;

    // Get the input stream
    InputStream is = new FileInputStream(dbCredsLocation);

    // Parse the properties
    Properties props = new Properties();
    props.load(is);
    is.close();

    String cacheClusterEndPoint = props.getProperty("cachecluster_endpoint");
    String cacheClusterPortStr = props.getProperty("cachecluster_port");
    Integer cacheClusterPort =
        cacheClusterPortStr == null ||  cacheClusterPortStr.isEmpty() 
        ? null : Integer.parseInt(cacheClusterPortStr);

    MemcachedCacheService.initialise(cacheClusterEndPoint, cacheClusterPort, 
        shutdownMonitor);

    logger.info(
        "Initialised cache connections to " + fileName + " using " + dbCredsLocation + " ("
            + System.getProperty("java.version") + ").");
    
    final String awsAccessKey = props.getProperty("aws.access.key");
    final String awsSecretKey = props.getProperty("aws.secret.key");
    String awsRegionString = props.getProperty("aws.region");
    String queue1Url = props.getProperty("queue1Url");
    queueUrlsByQueueType = new HashMap<>();
    queueUrlsByQueueType.put(DemoQueueType.QUEUE_1, queue1Url);
    
    AwsCredentials credentials = new AwsCredentials() {

      @Override
      public String accessKeyId() {
        return awsAccessKey;
      }

      @Override
      public String secretAccessKey() {
        return awsSecretKey;
      }
    };
  
    AwsCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(credentials);
    
    Region awsRegion = Region.of(awsRegionString);
  
    sqsClient = SqsClient
        .builder()
        .credentialsProvider(credentialsProvider)
        .region(awsRegion)
        .build();
    
  }

  /**
   * Closes the database connections to the mysql and the mongodb database
   */
  public static void shutdownCacheConnections() {
    MemcachedCacheService.shutdown();
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

            logger.debug(dequeuedCommand.getLogMessage(
                consumerExecutionContext, JobCommandEvent.JOB_COMPLETED_SUCCESSFULLY, 
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
