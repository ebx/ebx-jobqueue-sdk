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

import com.echobox.jobqueue.commands.JobCommand;
import com.echobox.jobqueue.context.JobCommandExecutionContext;

import java.io.Serializable;

/**
 * Convenience interface for PersistentJobOnQueue objects for situations where the job that has 
 * been added to the queue is a JobCommand that runs within a specified JobCommandExecutionContext
 * 
 * @author Michael Lavelle
 * 
 * @param <C> The type of JobCommandExecutionContext in which this command executes
 * @param <Q> The type of the queue-type identifier
 * @param <I> The type of unique identifier for each job on the queue
 *
 */
public interface PersistentJobCommandOnQueue<C extends JobCommandExecutionContext<C, Q, I>, 
    Q extends Serializable, I extends Serializable>
    extends PersistentJobOnQueue<Q, I, JobCommand<C>> {
}
