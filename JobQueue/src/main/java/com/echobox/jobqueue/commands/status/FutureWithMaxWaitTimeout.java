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

package com.echobox.jobqueue.commands.status;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Future decorator which enforces a maximum wait timeout which limits the amount of time
 * to wait for, irrespective of whether the client calls the get() method without specifying a
 * a timeout, or the get method which specifies a timeout.  In the later case the minimum of
 * the two timeouts will be used.
 *
 * @param <V>    the type parameter
 * @author Michael Lavelle
 */
public class FutureWithMaxWaitTimeout<V> implements Future<V> {

  private Future<V> decorated;
  private long maxWaitTimeout;
  private TimeUnit maxWaitTimeoutTimeUnit;

  /**
   * Instantiates a new Future with max wait timeout.
   *
   * @param decorated the decorated
   * @param maxWaitTimeout the max wait timeout
   * @param maxWaitTimeoutTimeUnit the max wait timeout time unit
   */
  public FutureWithMaxWaitTimeout(Future<V> decorated, long maxWaitTimeout,
      TimeUnit maxWaitTimeoutTimeUnit) {
    this.maxWaitTimeout = maxWaitTimeout;
    this.maxWaitTimeoutTimeUnit = maxWaitTimeoutTimeUnit;
    this.decorated = decorated;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#cancel(boolean)
   */
  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return decorated.cancel(mayInterruptIfRunning);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#isCancelled()
   */
  @Override
  public boolean isCancelled() {
    return decorated.isCancelled();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#isDone()
   */
  @Override
  public boolean isDone() {
    return decorated.isDone();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#get()
   */
  @Override
  public V get() throws InterruptedException, ExecutionException {
    try {
      return decorated.get(maxWaitTimeout, maxWaitTimeoutTimeUnit);
    } catch (TimeoutException e) {
      throw new ExecutionException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
   */
  @Override
  public V get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {

    long requestedTimeInSeconds = TimeUnit.SECONDS.convert(timeout, unit);
    long maxTimeInSeconds = TimeUnit.SECONDS.convert(maxWaitTimeout, maxWaitTimeoutTimeUnit);
    long timeInSeconds = Math.min(requestedTimeInSeconds, maxTimeInSeconds);

    return decorated.get(timeInSeconds, TimeUnit.SECONDS);
  }

}
