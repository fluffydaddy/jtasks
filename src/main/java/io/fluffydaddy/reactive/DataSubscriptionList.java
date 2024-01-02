package io.fluffydaddy.reactive;

import io.fluffydaddy.jutils.Array;

public class DataSubscriptionList implements DataSubscription {
	private final Array<DataSubscription> subscriptions = new Array<DataSubscription>();
	private boolean canceled;
	
	public synchronized boolean add(DataSubscription subscription) {
		canceled = false;
		return subscriptions.add(subscription);
	}
	
	public synchronized int getActiveSubscriptionCount() {
		return subscriptions.size();
	}
	
	@Override
	public synchronized void cancel() {
		canceled = true;
		//
		for (DataSubscription subscription : subscriptions) {
			subscription.cancel();
		}
		//
		subscriptions.clear();
	}

	@Override
	public synchronized boolean isCanceled() {
		return canceled;
	}
}
