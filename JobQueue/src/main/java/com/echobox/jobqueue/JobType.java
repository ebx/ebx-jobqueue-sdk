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

package com.echobox.jobqueue;

import java.io.Serializable;

/**
 * Represents a type of Job
 *
 * @param <T> The type of Enum we use to distinguish between different JobTypes
 * @author Michael Lavelle
 */
public abstract class JobType<T extends Enum<?>> implements Serializable {

  /**
   * Default serialization id
   */
  private static final long serialVersionUID = 1L;
  
  private T jobTypeEnum;

  /**
   * Instantiates a new Job type.
   *
   * @param jobTypeEnum The instance of the Enum we use to identify this type of Job
   */
  public JobType(T jobTypeEnum) {
    this.jobTypeEnum = jobTypeEnum;
  }

  /**
   * Gets job type enum.
   *
   * @return The jobType
   */
  public T getJobTypeEnum() {
    return jobTypeEnum;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((jobTypeEnum == null) ? 0 : jobTypeEnum.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    JobType<?> other = (JobType<?>) obj;
    if (jobTypeEnum == null) {
      if (other.jobTypeEnum != null) {
        return false;
      }
    } else if (!jobTypeEnum.equals(other.jobTypeEnum)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "JobCommandType [jobType=" + jobTypeEnum + "]";
  }
}
