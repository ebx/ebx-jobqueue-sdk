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

package com.echobox.jobqueue.status;

/**
 * Enumeration of different types of status a Job may be in.
 *
 * @author Michael Lavelle
 */
public enum JobStatusType {

  /**
   *Unknown job status type.
   */
  UNKNOWN, /**
   *Uncompleted with warning job status type.
   */
  UNCOMPLETED_WITH_WARNING, /**
   *Completed with error job status type.
   */
  COMPLETED_WITH_ERROR, /**
   *Completed without error job status type.
   */
  COMPLETED_WITHOUT_ERROR;
}
