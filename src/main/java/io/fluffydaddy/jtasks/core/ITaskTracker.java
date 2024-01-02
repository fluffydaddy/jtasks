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

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public interface ITaskTracker {
    /* ----------------- */
    /* ----- TRACK ----- */
    /* ----------------- */
    
    /**
     * Слежка за задачей. Может помочь в прочих расходных
     * Ресурсов и/или каких-либо затратных счетчиков.
     */
    void track(ITaskService task);
    
    /**
     * Разрушает связь между задачей и больше не следит
     * Что происходит с задачей спит ли она или просто ждет ответа.
     */
    void untrack(ITaskService task);
    
    /**
     * Начинает слежку.
     */
    void startTracking();
    
    /**
     * Теряет связь с задачей(ами).
     */
    List<ITaskService> stopTracking();
    
    /**
     * Ждёт выполнения всех задач.
     */
    Map<ITaskService, ?> awaitTermination();
    
    /**
     * Установит состояние слежки.
     */
    boolean setState(TrackState state, ITaskService from);
    
    /**
     * Проверить состояние в списке истории.
     */
    boolean hasState(TrackState state, ITaskService task);
    
    /**
     * Активное состояние слежки.
     */
    TrackState getState(ITaskService from);
    
    /**
     * Получить историю всех состояний задачи
     */
    TrackState[] getStates(ITaskService from);
    
    /**
     * Обновит тег трекера.
     */
    void updateTrackTag(String trackTag);
    
    /* ----------------- */
    /* ----- FLAGS ----- */
    /* ----------------- */
    
    /**
     * Активна ли слежка в данный момент?
     */
    boolean isTracking();
    
    /**
     * Получить тайм-аут времени для ожидания выполнения.
     */
    long getTimeout();
    
    /**
     * Получить тип времени для завершения задачи.
     */
    TimeUnit getTimeUnit();
    
    /**
     * Получить трек номер.
     * Также от части является конвеером
     * Для задач, в котором нуждается логирование.
     */
    String getTrackTag();
    
    /**
     * Очередь задач на выполнение(слежку).
     *
     * @see ITaskTracker#track(ITaskService)
     */
    Queue<ITaskService> getQueue();
    
    /**
     * Получить список активных задач.
     */
    List<ITaskService> getTasks();
}
