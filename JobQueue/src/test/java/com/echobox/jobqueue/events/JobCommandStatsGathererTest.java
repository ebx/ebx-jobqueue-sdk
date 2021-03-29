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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.echobox.jobqueue.JobType;
import com.echobox.jobqueue.commands.MockJobType;
import com.echobox.jobqueue.commands.MockQueuedJobCommand;
import com.echobox.jobqueue.commands.MockWorkerCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Tests for JobCommandStatsGatherer
 * @author eddspencer
 */
public class JobCommandStatsGathererTest {

  private final JobType<?> jobType1 = new MockJobType("jobType1");
  private final JobType<?> jobType2 = new MockJobType("jobType2");
  private final JobType<?> jobType3 = new MockJobType("jobType3");

  /**
   * Setup tests
   */
  @Before
  public void setup() {
    JobCommandStatsGatherer.setEnabled(true);
    JobCommandStatsGatherer.resetStats();
  }

  /**
   * Tear down tests
   */
  @After
  public void tearDown() {
    JobCommandStatsGatherer.setEnabled(false);
  }

  private void createTestCommands() {
    long now = 0;
    createCommand(jobType1, now, now + 5, "jobType1");
    createCommand(jobType1, now += 10, now + 10, "jobType1");
    createCommand(jobType1, now += 10, now + 15, "jobType1");
    createCommand(jobType2, now += 10, now + 20, "jobType2");
    createCommand(jobType2, now += 10, now + 30, "jobType2");
    createCommand(jobType3, now += 10, now + 80, "jobType3");
    createCommand(jobType3, now += 10, now + 100, "jobType3");

    // Create failed command to be ignored
    final MockWorkerCommand commandFailed = new MockWorkerCommand(jobType3, now);
    commandFailed.setUnsuccessfulCompletionUnixTime(now + 1, new Exception("Error"));
  }

  private MockQueuedJobCommand createCommand(final JobType<?> jobType, final long creationTimeUnix,
      final long completedTimeUnix, String queuedJob) {
    MockWorkerCommand mockedCommand = new MockWorkerCommand(jobType, creationTimeUnix);
    final MockQueuedJobCommand command = new MockQueuedJobCommand(null, jobType, 
        mockedCommand, creationTimeUnix);
    mockedCommand.setSuccessfulCompletionUnixTime(completedTimeUnix);
    return command;
  }

  /**
   * Check that the stats are gathered correctly, split by job type
   */
  @Test
  public void gatherStatsOnMultipleJobs() {
    createTestCommands();

    final Map<String, Map<String, Double>> stats = JobCommandStatsGatherer.getStats();
    final Map<String, Double> stats1 = stats.get(jobType1.toString());
    assertEquals(5.0, stats1.get("min"), 0.001);
    assertEquals(15.0, stats1.get("max"), 0.001);
    assertEquals(10.0, stats1.get("mean"), 0.001);
    assertEquals(3.0, stats1.get("count"), 0.001);
    assertEquals(25.0, stats.get(jobType2.toString()).get("mean"), 0.001);
    assertEquals(90.0, stats.get(jobType3.toString()).get("mean"), 0.001);
  }

  /**
   * Check that the stats gathering can handle concurrency
   */
  @Test
  public void handlesConcurrency() {
    LongStream.range(0, 1000).parallel().forEach(i -> createCommand(jobType1, i, i + 1, 
        "jobType"));

    final Map<String, Map<String, Double>> stats = JobCommandStatsGatherer.getStats();
    assertEquals(1000.0, stats.get(jobType1.toString()).get("count"), 0.001);
  }

  /**
   * Checks that reset stats clears the stored stats and that it does not effect previously 
   * gathered stats data
   */
  @Test
  public void resetStats() {
    createTestCommands();
    final Map<String, Map<String, Double>> statsBefore = JobCommandStatsGatherer.getStats();
    JobCommandStatsGatherer.resetStats();
    final Map<String, Map<String, Double>> statsAfter = JobCommandStatsGatherer.getStats();

    assertFalse(statsBefore.isEmpty());
    assertTrue(statsAfter.isEmpty());
  }
  
  /**
   * Checks that reset stats clears the stored stats and that it does not effect reading the stats
   * data
   */
  @Test
  public void resetStatsConcurrentTest() {
    final AtomicInteger countError = new AtomicInteger();
    createTestCommands();
    IntStream.range(0, 10).parallel().forEach(i -> {
      try {
        JobCommandStatsGatherer.getStats();
        JobCommandStatsGatherer.resetStats();
        JobCommandStatsGatherer.getStats();
      } catch (ConcurrentModificationException ex) {
        countError.getAndIncrement();
      } catch (Exception e) {
        countError.getAndIncrement();
      }
    });
    assertEquals(0, countError.get());
  }

}
