package io.fluffydaddy.reactive;

public interface DelegatingObserver<T> {
	DataObserver<T> getDelegate();
}
