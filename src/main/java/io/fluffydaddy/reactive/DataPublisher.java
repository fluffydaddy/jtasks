package io.fluffydaddy.reactive;

public interface DataPublisher<T> {
	void subscribe(DataObserver<T> observer, Object param);
	void publishSingle(DataObserver<T> observer, Object param);
	void unsubscribe(DataObserver<T> observer, Object param);
}
