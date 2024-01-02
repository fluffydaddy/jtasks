package io.fluffydaddy.reactive;

public interface DataSubscription {
	void cancel();
	boolean isCanceled();
}
