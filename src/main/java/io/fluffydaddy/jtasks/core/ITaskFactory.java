/*
 * Copyright (C) 2024 fluffydaddy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fluffydaddy.jtasks.core;

import io.fluffydaddy.jtasks.factory.Job;

import java.util.concurrent.Callable;

public abstract class ITaskFactory {
    public abstract <R> ITaskService<R, Void> createTask(Callable<R> job);
    
    public abstract <R, P> ITaskService<R, P> createTask(Job<R, P> job);
    
    public ITaskService<Void, Void> createTask(final Runnable job) {
        return createTask(() -> {
            job.run();
            return null;
        });
    }
}