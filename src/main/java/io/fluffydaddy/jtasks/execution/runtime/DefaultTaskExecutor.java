/*
 * Copyright © 2024 fluffydaddy
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
package io.fluffydaddy.jtasks.execution.runtime;

import io.fluffydaddy.annotation.NonNull;
import io.fluffydaddy.annotation.Nullable;
import io.fluffydaddy.jtasks.execution.ExecutorSignal;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultTaskExecutor extends TaskExecutor {
    private static final Thread MAIN_THREAD;
    
    static {
        MAIN_THREAD = Thread.currentThread();
    }
    
    private final Object mLock = new Object();
    
    private final ExecutorService mWorkIO = Executors.newFixedThreadPool(4, new ThreadFactory() {
        private static final String THREAD_NAME_STEM = "arch_work_io_";
        
        private final AtomicInteger mThreadId = new AtomicInteger(0);
        
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread t = new Thread(r);
            t.setName(THREAD_NAME_STEM + mThreadId.getAndIncrement());
            return t;
        }
    });
    
    @Nullable
    private volatile Executor mMainHandler;
    private volatile Thread mMainThread = MAIN_THREAD;
    
    @Override
    public void executeOnWorkIO(@NonNull Runnable runnable) {
        mWorkIO.execute(runnable);
    }
    
    @Override
    public void postToMainThread(@NonNull Runnable runnable) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = new ExecutorSignal();
                }
            }
        }
        //noinspection ConstantConditions
        mMainHandler.execute(runnable);
    }
    
    @Override
    public boolean isMainThread() {
        return mMainThread == Thread.currentThread();
    }
}
