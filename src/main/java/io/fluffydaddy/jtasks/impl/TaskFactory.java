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

package io.fluffydaddy.jtasks.impl;

import java.util.concurrent.Callable;

import io.fluffydaddy.jtasks.core.ITaskService;
import io.fluffydaddy.jtasks.core.ITaskFactory;
import io.fluffydaddy.jtasks.factory.Job;

public class TaskFactory extends ITaskFactory {
    @Override
    public <R> ITaskService<R, Void> createTask(final Callable<R> job) {
        return createTask(new Job<>() {
            @Override
            public R doInBackground(Void param) throws Exception {
                return job.call();
            }

            @Override
            public void onBeginning() {

            }

            @Override
            public void onFinishing(R result) {
            }
        });
    }

    @Override
    public <R, P> ITaskService<R, P> createTask(final Job<R, P> job) {
        return new TaskService<>() {
            @Override
            public final R doInBackground(P param) throws Exception {
                return job.doInBackground(param);
            }

            @Override
            public void onExecute() {
                job.onBeginning();
            }

            @Override
            public void onComplete(R result) {
                job.onFinishing(result);
            }
        };
    }
}
