package io.fluffydaddy.reactive;

import io.fluffydaddy.jutils.Unit;

public interface DataSubscriber<L> extends DataSubscription, Iterable<L> {
    void subscribe(L observer);
    void unsubscribe(L observer);
    void cancel();
	
    void forEach(Unit<? super L> consumer);
}
