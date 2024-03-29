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

import com.echobox.jobqueue.CoreJobType;
import com.echobox.jobqueue.JobType;

/**
 * The type Mock job type.
 * @author Michael Lavelle
 */
public class MockJobType extends JobType<CoreJobType> {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private String name;

  /**
   * Instantiates a new Mock job type.
   *
   * @param name the name
   */
  public MockJobType(String name) {
    super(CoreJobType.QUEUED_JOB);
    this.name = name;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MockJobType [name=" + name + "]";
  }

}
