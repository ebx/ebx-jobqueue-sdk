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

import com.echobox.time.UnixTime;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Listens to completed jobs and gathers stats on their completion times by queue type
 *
 * @param <J> job type
 * @author eddspencer
 */
public class JobCommandStatsGatherer<J extends Serializable>
    implements JobCommandCompletionListener {

  /**
   * Global stats for the queue to completion time of jobs 
   */
  private static final Map<String, DescriptiveStatistics> STATS = new ConcurrentHashMap<>();
  /**
   * Flag for the enabled state
   */
  private static volatile boolean ENABLED = true;

  private final String jobType;
  private long creationTimeUnix;

  /**
   * Constructor setting queue type to add stats to 
   *
   * @param jobType job type
   * @param creationTimeUnix unix time job was created
   */
  public JobCommandStatsGatherer(final J jobType, final long creationTimeUnix) {
    this.jobType = jobType.toString();
    this.creationTimeUnix = creationTimeUnix;
  }

  /**
   * Reset the currently collected stats
   */
  public static void resetStats() {
    synchronized (STATS) {
      STATS.clear();
    }
  }

  /**
   * Gets the currently collected stats
   *
   * @return the stats: a map keyed by job type with value being a map of stat -> value pairs 
   * for: count, min, max, median, mean, 95th percentile and 99th percentile.
   */
  public static Map<String, Map<String, Double>> getStats() {
    return STATS.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
      final DescriptiveStatistics stats = e.getValue();

      final Map<String, Double> data = new HashMap<>();
      data.put("count", (double) stats.getN());
      data.put("min", stats.getMin());
      data.put("max", stats.getMax());
      data.put("median", stats.getPercentile(50.0));
      data.put("mean", stats.getMean());
      data.put("95percentile", stats.getPercentile(95.0));
      data.put("99percentile", stats.getPercentile(99.0));
      return data;
    }));
  }

  /**
   * Enables or disables the stats gatherer
   * 
   * @param enabled whether to gather stats or not
   */
  public static void setEnabled(final boolean enabled) {
    ENABLED = enabled;
  }

  /**
   * If enabled then the listeners will be added to job commands and stats will be gathered
   *
   * @return whether to gather stats on job commands
   */
  public static boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void setSuccessfulCompletionUnixTime(final long completedUnixTime) {
    synchronized (STATS) {
      DescriptiveStatistics stats = STATS.get(jobType);
      if (stats == null) {
        stats = new DescriptiveStatistics();
        STATS.put(jobType, stats);
      }
      stats.addValue(completedUnixTime - creationTimeUnix);
    }
  }

  @Override
  public void setUnsuccessfulCompletionUnixTime(final long completedUnixTime,
      final Exception exception) {
    // Ignore unsuccessful completion
  }

  @Override
  public void resetCompletionStatus() {
    // When reset it is as if the job has been re-created
    this.creationTimeUnix = UnixTime.now();
  }

}
