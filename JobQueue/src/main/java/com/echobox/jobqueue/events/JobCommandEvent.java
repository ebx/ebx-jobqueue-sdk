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

/**
 * Enum for the different types of event we may wish to log during the lifecycle of a JobCommand
 *
 * @author Michael Lavelle
 */
public enum JobCommandEvent {

  /**
   *Job started job command event.
   */
  JOB_STARTED, /**
   *Job initialisation job command event.
   */
  JOB_INITIALISATION, /**
   *Job completed successfully job command event.
   */
  JOB_COMPLETED_SUCCESSFULLY, /**
   *Job error job command event.
   */
  JOB_ERROR, /**
   *Job info job command event.
   */
  JOB_INFO, /**
   *Job completed unsuccessfully job command event.
   */
  JOB_COMPLETED_UNSUCCESSFULLY
}
