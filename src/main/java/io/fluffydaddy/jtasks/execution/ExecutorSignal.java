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

package io.fluffydaddy.jtasks.execution;

import io.fluffydaddy.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class ExecutorSignal implements Executor {
    /**
     * An {@link Executor} that executes tasks one at a time in serial
     * order.  This serialization is global to a particular process.
     */
    public static final Executor SERIAL_EXECUTOR = new ExecutorSerial();
    
    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
    
    private final Queue<Runnable> messageQueue = new LinkedList<>();
    private final Object messageLock = new Object();
    private boolean isHandlingMessages = false;
    
    public void sendToTarget() {
        while (true) {
            Runnable runnable;
            synchronized (messageLock) {
                runnable = messageQueue.poll();
                if (runnable == null) {
                    isHandlingMessages = false;
                    break;
                }
            }
            sDefaultExecutor.execute(runnable);
        }
    }
    
    @Override
    public void execute(@NonNull Runnable runnable) {
        synchronized (messageLock) {
            messageQueue.offer(runnable);
            if (!isHandlingMessages) {
                isHandlingMessages = true;
                sendToTarget();
            }
        }
    }
    
    public static void setDefaultExecutor(Executor executor) {
        sDefaultExecutor = executor == null ? SERIAL_EXECUTOR : executor;
    }
    
    public static Executor getDefaultExecutor() {
        return sDefaultExecutor;
    }
}
