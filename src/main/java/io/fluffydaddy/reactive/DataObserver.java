package io.fluffydaddy.reactive;

public interface DataObserver<T> {
	void onData(T data);
}
