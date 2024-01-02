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

/**
 * Класс является информатором жизненного цикла задачи.
 *
 * <p>
 * Состояния когда задача приняла получила некий триггер
 * на отслеживание событий или иных действий внутри жизненного цикла.
 * </p>
 *
 * @see ITaskTracker
 */
public enum TrackState {
    /**
     * Состояние когда задачу отменили от выполнения.
     *
     * @see ITaskService#onCanceled(Object)
     */
    CANCELED,
    
    /**
     * Состояние ожидания когда задача вот-вот запустится.
     *
     * @see ITaskService#onExecute()
     */
    TRACKING,
    
    /**
     * Состояние когда задача разрешила нам ее отслеживать.
     *
     * @see ITaskService#submit(ITaskTracker)
     */
    SUBMITTED,
    
    /**
     * Состояние когда задача запретила нам ее отслеживать.
     *
     * @see ITaskService#cancel(ITaskTracker)
     */
    UNTRACKED,
    
    /**
     * Состояние когда задача завершила свою работу.
     *
     * @see ITaskService#onComplete(Object)
     */
    FINISHED,
    
    /**
     * Состояние когда задача прервалась из-за ошибки.
     *
     * @see ITaskService#onException(Throwable)
     */
    EXCEPTED,
    
    /**
     * Состояние когда задаче пришло событие на прерывание своего цикла.
     *
     * @see ITaskService#onTerminate()
     */
    TERMINATED;
}
