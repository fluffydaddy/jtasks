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

package io.fluffydaddy.jtasks.execution;

import io.fluffydaddy.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class ExecutorFactory implements ThreadFactory {
    private final ThreadFactory mFactory;
    
    private Thread.UncaughtExceptionHandler _crashHandler;
    private String _name;
    private boolean _daemon;
    
    public ExecutorFactory(ThreadFactory defFactory) {
        mFactory = defFactory;
    }
    
    public ExecutorFactory() {
        this(Executors.defaultThreadFactory());
    }
    
    public ExecutorFactory(ThreadFactory defFactory, Thread.UncaughtExceptionHandler crashHandler, String name, boolean daemon) {
        this(defFactory);
        
        _crashHandler = crashHandler;
        _name = name;
        _daemon = daemon;
    }
    
    @Override
    public Thread newThread(@NonNull Runnable command) {
        Thread thread = mFactory.newThread(command);
        
        if (_crashHandler != null) {
            thread.setUncaughtExceptionHandler(_crashHandler);
        }
        
        if (_name != null) {
            thread.setName(_name);
        }
        
        if (_daemon) {
            thread.setDaemon(_daemon);
        }
        
        return thread;
    }
    
    public void setCrashHandler(Thread.UncaughtExceptionHandler crashHandler) {
        _crashHandler = crashHandler;
    }
    
    public Thread.UncaughtExceptionHandler getCrashHandler() {
        return _crashHandler;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public String getName() {
        return _name;
    }
    
    public void setDaemon(boolean on) {
        _daemon = on;
    }
    
    public boolean isDaemon() {
        return _daemon;
    }
}
