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

package io.fluffydaddy.jtasks.core;

import io.fluffydaddy.jtasks.execution.ExecutorHandler;
import io.fluffydaddy.reactive.DataSubscription;
import io.fluffydaddy.reactive.livedata.LiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * An interface defining a service for executing tasks with specified parameters.
 *
 * @param <R> The result type of the task execution.
 * @param <P> The parameter type of the task execution.
 */
public interface ITaskService<R, P> extends ExecutorHandler<R, P>, Executor, DataSubscription {
    /**
     * Checks if the task is still alive.
     *
     * @return True if the task is alive, false otherwise.
     * @see Thread#isAlive()
     */
    boolean isAlive();
    
    /**
     * Gets the name of the task.
     *
     * @return The name of the task.
     * @see Thread#getName()
     */
    String getName();
    
    /**
     * Checks if the task is complete, indicating successful execution.
     *
     * @return True if the task is complete, false otherwise.
     * @see ExecutorHandler#onComplete(Object)
     */
    boolean isComplete();
    
    /**
     * Checks if the task has been canceled by the user.
     *
     * @return True if the task is canceled, false otherwise.
     * @see ITaskService#cancel()
     */
    boolean isCanceled();
    
    /**
     * Checks if the task is a daemon thread and whether it makes sense
     * to wait for its completion or expend any overhead resources.
     *
     * @return True if the task is a daemon thread, false otherwise.
     * @see Thread#isDaemon()
     */
    boolean isDaemon();
    
    /**
     * Gets the result of the task execution.
     *
     * @return The result of the task execution.
     * @see Future#get()
     */
    R get();
    
    /**
     * Gets the result of the task execution within a specified timeout period.
     *
     * @param timeOut   The maximum time to wait for the result.
     * @param timeInUnit The time unit of the timeout argument.
     * @return The result of the task execution.
     * @see Future#get(long, TimeUnit)
     */
    R get(long timeOut, TimeUnit timeInUnit);
    
    /**
     * Submits the task to a tracker if the tracker has sent a notification about attachment.
     *
     * @param tracker The task tracker to submit to.
     * @see ITaskTracker#track(ITaskService)
     */
    void submit(ITaskTracker tracker);
    
    /**
     * Cancels the task and untracks it from a tracker if the tracker has sent a notification about untracking.
     *
     * @param tracker The task tracker to cancel and untrack from.
     * @see ITaskTracker#untrack(ITaskService)
     */
    void cancel(ITaskTracker tracker);
    
    /**
     * Executes the task with the provided parameters.
     *
     * @param param The parameter for the task execution.
     * @see Executor#execute(Runnable)
     */
    void execute(P param);
    
    /**
     * Executes the task without any parameters.
     *
     * @see ITaskService#execute(Object)
     */
    void execute();
    
    /**
     * Shuts down the task, waiting for it to complete within the specified timeout.
     *
     * @param timeout The maximum time to wait for the task to complete.
     * @param unit    The time unit of the timeout argument.
     * @see Future#get(long, TimeUnit)
     */
    void shutdown(long timeout, TimeUnit unit);
    
    /**
     * Forces the task to complete.
     *
     * @see Future#cancel(boolean)
     */
    void destroy();
    
    /**
     * Sets whether the task is a daemon thread.
     *
     * @param on True if the task should be a daemon thread, false otherwise.
     * @see Thread#setDaemon(boolean)
     */
    void setDaemon(boolean on);
    
    /**
     * Sets the name of the task.
     *
     * @param name The name of the task.
     * @see Thread#setName(String)
     */
    void setName(String name);
    
    /**
     * Sets or adds the active state in the task's lifecycle.
     *
     * @param state The state to set or add.
     * @return True if the state was successfully set or added, false otherwise.
     * @see ITaskTracker#setState(TrackState, ITaskService)
     */
    boolean setState(TrackState state);
    
    /**
     * Checks if the current active state is equal to the provided state.
     *
     * @param state The state to check.
     * @return True if the active state is equal to the provided state, false otherwise.
     * @see ITaskTracker#hasState(TrackState, ITaskService)
     */
    boolean hasState(TrackState state);
    
    /**
     * Gets the current active state of the task.
     *
     * @return The current active state of the task.
     */
    TrackState getActiveState();
    
    /**
     * Observes the live state changes of the task.
     *
     * @return The live data object containing the task's state.
     */
    LiveData<TrackState> getLiveState();
    
    /**
     * Gets the lock used for synchronization.
     *
     * @return The lock used for synchronization.
     */
    Lock getLock();
    
    /**
     * Sets the uncaught exception handler for the task.
     *
     * @param uncaughtCrashHandler The uncaught exception handler.
     * @see Thread#setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)
     */
    void setCrashHandler(Thread.UncaughtExceptionHandler uncaughtCrashHandler);
    
    /**
     * Gets the uncaught exception handler for the task.
     *
     * @return The uncaught exception handler.
     * @see Thread#getUncaughtExceptionHandler()
     */
    Thread.UncaughtExceptionHandler getCrashHandler();
    
    /**
     * Sets the handler for executing various tasks on the main thread.
     *
     * @param mainExecutor The executor for the main thread.
     */
    void setMainExecutor(Executor mainExecutor);
    
    /**
     * Sets the handler for executing various tasks on the specified thread.
     *
     * @param taskExecutor The executor for the specified thread.
     */
    void setTaskExecutor(Executor taskExecutor);
    
    /**
     * Gets the handler for executing various tasks on the main thread.
     *
     * @return The executor for the main thread.
     */
    Executor getMainExecutor();
    
    /**
     * Gets the handler for executing various tasks on the specified thread.
     *
     * @return The executor for the specified thread.
     */
    Executor getTaskExecutor();
    
    /**
     * Notifies about an exception.
     *
     * @param thread The thread where the exception occurred.
     * @param cause  The exception causing the error.
     * @see Thread.UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
     */
    void handleException(Thread thread, Throwable cause);
    
    /**
     * Notifies about an exception in the current thread.
     *
     * @param cause The exception causing the error.
     * @see Thread.UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
     */
    void handleException(Throwable cause);
    
    /**
     * Sleeps for the specified duration in milliseconds.
     *
     * @param millis The duration to sleep in milliseconds.
     * @return True if the sleep was successful, false otherwise.
     * @see Thread#sleep(long)
     */
    boolean await(long millis);
    
    /**
     * Sleeps for the specified duration in milliseconds and nanoseconds.
     *
     * @param millis The duration to sleep in milliseconds.
     * @param nanos  The additional duration in nanoseconds.
     * @return True if the sleep was successful, false otherwise.
     * @see Thread#sleep(long, int)
     */
    boolean await(long millis, int nanos);
}
