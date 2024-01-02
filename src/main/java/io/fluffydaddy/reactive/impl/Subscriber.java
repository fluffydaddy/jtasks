package io.fluffydaddy.reactive.impl;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import io.fluffydaddy.jutils.Array;
import io.fluffydaddy.jutils.Unit;
import io.fluffydaddy.reactive.DataSubscriber;

/**
 * Класс является observable, наблюдает за изменением данных.
 * */
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
    @NonNull
    @Override
    public Iterator<L> iterator() {
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
