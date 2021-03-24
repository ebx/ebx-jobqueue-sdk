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

package com.echobox.jobqueue.exceptions;

/**
 * Utility class to aid formatting of stack traces
 * 
 * @author Michael Lavelle
 */
public class JobQueueExceptionFormatter {

  /**
   * Get a formatted stack trace of the provided exception
   * @param ex The exception
   * @return The stack trace as a string
   */
  public static String getStackTraceString(Exception ex) {

    StackTraceElement[] elements = ex.getStackTrace();
    String result = "";
    for (int i = 0; i < elements.length; i++) {
      result += elements[i] + "\n";
    }
    return result;
  }
}
