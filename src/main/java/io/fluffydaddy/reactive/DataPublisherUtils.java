package io.fluffydaddy.reactive;

import java.util.Collection;

public class DataPublisherUtils {
    public static <T> void removeObserverFromCopyOnWriteSet(Collection<DataObserver<T>> observers, DataObserver<T> observer) {
        if (observers != null) {
            for (DataObserver<T> candidate : observers) {
                if (candidate.equals(observer)) {
                    // Unsupported by CopyOnWriteArraySet: iterator.remove();
                    observers.remove(candidate);
                } else if (candidate instanceof DelegatingObserver) {
                    DataObserver<T> delegate = candidate;
                    while (delegate instanceof DelegatingObserver) {
                        delegate = ((DelegatingObserver) delegate).getDelegate();
                    }
                    if (delegate == null || delegate.equals(observer)) {
                        // Unsupported by CopyOnWriteArraySet: iterator.remove();
                        observers.remove(candidate);
                    }
                }
            }
        }
    }
}
