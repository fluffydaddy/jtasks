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

import io.fluffydaddy.jtasks.execution.ExecutorHandler;
import io.fluffydaddy.reactive.DataSubscription;
import io.fluffydaddy.reactive.livedata.LiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface ITaskService<R, P> extends ExecutorHandler<R, P>, Executor, DataSubscription {
    /**
     * Жизненный цикл задачи, т.е. живет
     * Ли еще эта задача или нет.
     *
     * @see Thread#isAlive()
     */
    boolean isAlive();
    
    /**
     * Получит название задачи.
     *
     * @see Thread#getName()
     */
    String getName();
    
    /**
     * Выполнена ли задача, самопроизвольно.
     * Говорит нам о том, что задача выполнена успешно.
     *
     * @see ITaskService#onComplete(R)
     */
    boolean isComplete();
    
    /**
     * Отменена ли задача пользователем.
     *
     * @see ITaskService#cancel()
     */
    boolean isCanceled();
    
    /**
     * Является ли данная задача фоновой и/или имеет ли смысл
     * Ждать его завершения, а также тратить какие-нибудь накладные ресурсы.
     *
     * @see Thread#isDaemon()
     */
    boolean isDaemon();
    
    /**
     * Получить результат выполнения задачи.
     *
     * @see Future#get()
     */
    R get();
    
    /**
     * Получить результат выполнения задачи за определенный
     * Промежуток тайм-аут времени.
     *
     * @see Future#get(long, TimeUnit)
     */
    R get(long timeInOut, TimeUnit timeInUnit);
    
    /**
     * Если tracker отправил уведомление,
     * О прикреплении к себе задачи, то можем
     * Выполнить какое-то действие.
     *
     * @see ITaskTracker#track(ITaskService)
     */
    void submit(ITaskTracker tracker);
    
    /**
     * Tracker отправил на уведомление о том,
     * Что он больше не будет отслеживать
     * Все события, которые происходят
     * Внутри задачи, а также останавливает
     * И сбрасывает все накопленные счётчики.
     *
     * @see ITaskTracker#untrack(ITaskService)
     */
    void cancel(ITaskTracker tracker);
    
    /**
     * Аргументы передаются задаче, которая вызвала.
     *
     * @see Executor#execute(Runnable)
     */
    void execute(P param);
    
    /**
     * Запуск задачи без аргументов.
     *
     * @see ITaskService#execute(P)
     */
    void execute();
    
    /**
     * Завершение выполнения задачи.
     *
     * @see Future#get(long, TimeUnit)
     */
    void shutdown(long timeout, TimeUnit unit);
    
    /**
     * Принудительное завершение задачи.
     *
     * @see Future#cancel(boolean)
     */
    void destroy();
    
    /**
     * Создает процесс (службу), которая крутится в фоновом потоке.
     * Не нуждается в ожидании завершения.
     *
     * @see Thread#setDaemon(boolean)
     */
    void setDaemon(boolean on);
    
    /**
     * Установит название задачи.
     *
     * @see Thread#setName(String)
     */
    void setName(String name);
    
    /**
     * Установит/добавит активное состояние в цикле задачи.
     *
     * @see ITaskTracker#setState(TrackState, ITaskService)
     */
    boolean setState(TrackState state);
    
    /**
     * Является state равным текущему?
     *
     * @see ITaskTracker#hasState(TrackState, ITaskService)
     */
    boolean hasState(TrackState state);
    
    /**
     * Получить активное состояние.
     */
    TrackState getActiveState();
    
    /**
     * Наблюдение за активным состоянием задачи.
     */
    LiveData<TrackState> getLiveState();
    
    /**
     * Получить синхранизацию / монитор
     */
    Lock getLock();
    
    /**
     * @see Thread#setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)
     */
    void setCrashHandler(Thread.UncaughtExceptionHandler uncaughtCrashHandler);
    
    /**
     * @see Thread#getUncaughtExceptionHandler()
     */
    Thread.UncaughtExceptionHandler getCrashHandler();
    
    /**
     * Установит хендлер для запуска различных задач на выполнение в основном потоке.
     */
    void setMainExecutor(Executor mainExecutor);
    
    /**
     * Установит хендлер для запуска различных задач на выполнение в выбранном потоке.
     */
    void setTaskExecutor(Executor taskExecutor);
    
    /**
     * Получит хендлер для запуска различных задач на выполнение в основном потоке.
     */
    Executor getMainExecutor();
    
    /**
     * Получит хендлер для запуска различных задач на выполнение в выбранном потоке.
     */
    Executor getTaskExecutor();
    
    /**
     * Уведомить об ошибке.
     *
     * @see Thread.UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
     */
    void handleException(Thread thread, Throwable cause);
    
    /**
     * Уведомить об ошибке в текущем потоке.
     *
     * @see Thread.UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
     */
    void handleException(Throwable cause);
    
    /**
     * @see Thread#sleep(long)
     */
    boolean await(long millis);
    
    /**
     * @see Thread#sleep(long, int)
     */
    boolean await(long millis, int nanos);
}
