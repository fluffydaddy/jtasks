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

/**
 * An interface representing a handler for executing tasks with specified parameters.
 *
 * @param <R> The result type of the task execution.
 * @param <P> The parameter type of the task execution.
 */
public interface ExecutorHandler<R, P> {
    /**
     * Invoked when the operation is completed successfully.
     *
     * @param result The result of the completed operation.
     */
    void onComplete(R result);
    
    /**
     * Invoked when the operation is canceled.
     *
     * @param result The result associated with the canceled operation.
     */
    void onCanceled(R result);
    
    /**
     * Invoked before starting the task execution.
     */
    void onExecute();
    
    /**
     * Invoked when the operation is completed with an error.
     *
     * @param cause The exception causing the error in the operation.
     */
    void onException(Throwable cause);
    
    /**
     * Invoked when there is a request to terminate the operation.
     */
    void onTerminate();
    
    /**
     * Executes the task in the background thread.
     *
     * @param param The parameter for the task execution.
     * @return The result of the task execution.
     * @throws Exception If an exception occurs during the task execution.
     */
    R doInBackground(P param) throws Exception;
}
