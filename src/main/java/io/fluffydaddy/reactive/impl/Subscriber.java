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

package io.fluffydaddy.reactive.impl;

import io.fluffydaddy.jutils.collection.Array;
import io.fluffydaddy.jutils.collection.Unit;
import io.fluffydaddy.reactive.DataSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Класс является observable, наблюдает за изменением данных.
 */
public class Subscriber<L> implements DataSubscriber<L> {
    private final Array<L> observers = new Array<>();
    
    /**
     * @param observer наблюдает за данными.
     */
    @Override
    public void subscribe(L observer) {
        observers.add(observer);
    }
    
    /**
     * @param observer наблюдает за данными.
     */
    @Override
    public void unsubscribe(L observer) {
        observers.remove(observer);
    }
    
    /**
     * Отменить наблюдение за данными.
     */
    @Override
    public void cancel() {
        observers.clear();
    }
    
    /**
     * @return If observers not empty true else false.
     */
    @Override
    public boolean isCanceled() {
        return !observers.isEmpty();
    }
    
    /**
     * @see #forEach(Consumer)
     */
    @Override
    public void forEach(Unit<? super L> consumer) {
        observers.forEach(consumer);
    }
    
    /**
     * @see Array#iterator()
     */
    @Override
    public @NotNull Iterator<L> iterator() {
        return observers.iterator();
    }
    
    /**
     * @see Array#spliterator()
     */
    @Override
    public Spliterator<L> spliterator() {
        return observers.spliterator();
    }
}
