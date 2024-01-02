package io.fluffydaddy.reactive;

public class DataSubscriptionImpl<T> implements DataSubscription {
	private volatile boolean cancelled;
	private DataPublisher<T> publisher;
	private Object publisherParam;
	private DataObserver<T> observer;
	
	public DataSubscriptionImpl(DataPublisher<T> publisher, Object publisherParam, DataObserver<T> observer) {
		this.publisher = publisher;
		this.publisherParam = publisherParam;
		this.observer = observer;
	}
	
	@Override
	public void cancel() {
		cancelled = true;
		if (publisher != null) {
			publisher.unsubscribe(observer, publisherParam);
			
			publisher = null;
			observer = null;
			publisherParam = null;
		}
	}
	
	@Override
	public boolean isCanceled() {
		return cancelled;
	}
}
