package io.fluffydaddy.reactive;

public interface DataTransformer<F, T> {
	T transform(F source);
}
