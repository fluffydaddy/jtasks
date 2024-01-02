package io.fluffydaddy.reactive;

public interface Scheduler {
	<T> void schedule(Reactive<T> reactive, T param);
}
