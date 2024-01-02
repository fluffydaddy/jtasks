/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fluffydaddy.reactive.livedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.fluffydaddy.jtasks.execution.runtime.ArchTaskExecutor;
import androidx.arch.core.internal.SafeIterableMap;

import java.util.Iterator;
import java.util.Map;

import io.fluffydaddy.reactive.DataObserver;

/**
 * <p>
 * In addition, LiveData has {@link LiveData#onActive()} and {@link LiveData#onInactive()} methods
 * to get notified when number of active {@link DataObserver}s change between 0 and 1.
 * This allows LiveData to release any heavy resources when it does not have any Observers that
 * are actively observing.
 */
public abstract class LiveData<T> {
	@SuppressWarnings("WeakerAccess") /* synthetic access */
	final Object mDataLock = new Object();
	static final int START_VERSION = -1;
	@SuppressWarnings("WeakerAccess") /* synthetic access */
	static final Object NOT_SET = new Object();

	private SafeIterableMap<DataObserver<? super T>, ObserverWrapper> mObservers =
			new SafeIterableMap<>();

	// how many observers are in active state
	@SuppressWarnings("WeakerAccess") /* synthetic access */
			int mActiveCount = 0;
	// to handle active/inactive reentry, we guard with this boolean
	private boolean mChangingActiveState;
	private volatile Object mData;
	// when setData is called, we set the pending data and actual data swap happens on the main
	// thread
	@SuppressWarnings("WeakerAccess") /* synthetic access */
	volatile Object mPendingData = NOT_SET;
	private int mVersion;

	private boolean mDispatchingValue;
	@SuppressWarnings("FieldCanBeLocal")
	private boolean mDispatchInvalidated;
	private final Runnable mPostValueRunnable = () -> {
		Object newValue;
		synchronized (mDataLock) {
			newValue = mPendingData;
			mPendingData = NOT_SET;
		}
		setValue((T) newValue);
	};

	/**
	 * Creates a LiveData initialized with the given {@code value}.
	 *
	 * @param value initial value
	 */
	public LiveData(T value) {
		mData = value;
		mVersion = START_VERSION + 1;
	}

	/**
	 * Creates a LiveData with no value assigned to it.
	 */
	public LiveData() {
		mData = NOT_SET;
		mVersion = START_VERSION;
	}

	@SuppressWarnings("unchecked")
	private void considerNotify(ObserverWrapper observer) {
		if (!observer.mActive) {
			return;
		}
		// Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
		//
		// we still first check observer.active to keep it as the entrance for events. So even if
		// the observer moved to an active state, if we've not received that event, we better not
		// notify for a more predictable notification order.
		if (!observer.shouldBeActive()) {
			observer.activeStateChanged(false);
			return;
		}
		if (observer.mLastVersion >= mVersion) {
			return;
		}
		observer.mLastVersion = mVersion;
		observer.mObserver.onData((T) mData);
	}

	@SuppressWarnings("WeakerAccess") /* synthetic access */
	void dispatchingValue(@Nullable ObserverWrapper initiator) {
		if (mDispatchingValue) {
			mDispatchInvalidated = true;
			return;
		}
		mDispatchingValue = true;
		do {
			mDispatchInvalidated = false;
			if (initiator != null) {
				considerNotify(initiator);
				initiator = null;
			} else {
				for (Iterator<Map.Entry<DataObserver<? super T>, ObserverWrapper>> iterator =
					 mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
					considerNotify(iterator.next().getValue());
					if (mDispatchInvalidated) {
						break;
					}
				}
			}
		} while (mDispatchInvalidated);
		mDispatchingValue = false;
	}

	/**
	 * This means that the given observer will receive all events and will never
	 * be automatically removed. You should manually call {@link #removeObserver(DataObserver)} to stop
	 * observing this LiveData.
	 * While LiveData has one of such observers, it will be considered
	 * as active.
	 * <p>
	 * If the observer was already added with an owner to this LiveData, LiveData throws an
	 * {@link IllegalArgumentException}.
	 *
	 * @param observer The observer that will receive the events
	 */
	@MainThread
	public void observeForever(@NonNull DataObserver<? super T> observer) {
		assertMainThread("observeForever");
		AlwaysActiveObserver wrapper = new AlwaysActiveObserver(observer);
		ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
		if (existing != null) {
			return;
		}
		wrapper.activeStateChanged(true);
	}

	/**
	 * Removes the given observer from the observers list.
	 *
	 * @param observer The Observer to receive events.
	 */
	@MainThread
	public void removeObserver(@NonNull final DataObserver<? super T> observer) {
		assertMainThread("removeObserver");
		ObserverWrapper removed = mObservers.remove(observer);
		if (removed == null) {
			return;
		}
		removed.detachObserver();
		removed.activeStateChanged(false);
	}

	/**
	 * Posts a task to a main thread to set the given value. So if you have a following code
	 * executed in the main thread:
	 * <pre class="prettyprint">
	 * liveData.postValue("a");
	 * liveData.setValue("b");
	 * </pre>
	 * The value "b" would be set at first and later the main thread would override it with
	 * the value "a".
	 * <p>
	 * If you called this method multiple times before a main thread executed a posted task, only
	 * the last value would be dispatched.
	 *
	 * @param value The new value
	 */
	protected void postValue(T value) {
		boolean postTask;
		synchronized (mDataLock) {
			postTask = mPendingData == NOT_SET;
			mPendingData = value;
		}
		if (!postTask) {
			return;
		}
		ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
	}

	/**
	 * Sets the value. If there are active observers, the value will be dispatched to them.
	 * <p>
	 * This method must be called from the main thread. If you need set a value from a background
	 * thread, you can use {@link #postValue(Object)}
	 *
	 * @param value The new value
	 */
	@MainThread
	protected void setValue(T value) {
		assertMainThread("setValue");
		mVersion++;
		mData = value;
		dispatchingValue(null);
	}

	/**
	 * Returns the current value.
	 * <p>
	 * Note that calling this method on a background thread does not guarantee that the latest
	 * value set will be received.
	 *
	 * @return the current value or null if {@link #isInitialized()} is false
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public T getValue() {
		Object data = mData;
		if (data != NOT_SET) {
			return (T) data;
		}
		return null;
	}

	/**
	 * Returns whether an explicit value has been set on this LiveData. If this returns
	 * <code>true</code>, then the current value can be retrieved from {@link #getValue()}.
	 * <p>
	 * Note that calling this method on a background thread may still result in this method
	 * returning <code>false</code> even if a call to {@link #postValue(Object)} is being
	 * processed.
	 *
	 * @return whether an explicit value has been set on this LiveData
	 */
	public boolean isInitialized() {
		return mData != NOT_SET;
	}

	int getVersion() {
		return mVersion;
	}

	/**
	 * Called when the number of active observers change from 0 to 1.
	 * <p>
	 * This callback can be used to know that this LiveData is being used thus should be kept
	 * up to date.
	 */
	protected void onActive() {

	}

	/**
	 * Called when the number of active observers change from 1 to 0.
	 * You can check if there are observers via {@link #hasObservers()}.
	 */
	protected void onInactive() {

	}

	/**
	 * Returns true if this LiveData has observers.
	 *
	 * @return true if this LiveData has observers
	 */
	@SuppressWarnings("WeakerAccess")
	public boolean hasObservers() {
		return mObservers.size() > 0;
	}

	/**
	 * Returns true if this LiveData has active observers.
	 *
	 * @return true if this LiveData has active observers
	 */
	@SuppressWarnings("WeakerAccess")
	public boolean hasActiveObservers() {
		return mActiveCount > 0;
	}

	@MainThread
	void changeActiveCounter(int change) {
		int previousActiveCount = mActiveCount;
		mActiveCount += change;
		if (mChangingActiveState) {
			return;
		}
		mChangingActiveState = true;
		try {
			while (previousActiveCount != mActiveCount) {
				boolean needToCallActive = previousActiveCount == 0 && mActiveCount > 0;
				boolean needToCallInactive = previousActiveCount > 0 && mActiveCount == 0;
				previousActiveCount = mActiveCount;
				if (needToCallActive) {
					onActive();
				} else if (needToCallInactive) {
					onInactive();
				}
			}
		} finally {
			mChangingActiveState = false;
		}
	}

	private abstract class ObserverWrapper {
		final DataObserver<? super T> mObserver;
		boolean mActive;
		int mLastVersion = START_VERSION;

		ObserverWrapper(DataObserver<? super T> observer) {
			mObserver = observer;
		}

		abstract boolean shouldBeActive();

		void detachObserver() {
		}

		void activeStateChanged(boolean newActive) {
			if (newActive == mActive) {
				return;
			}
			// immediately set active state, so we'd never dispatch anything to inactive
			// owner
			mActive = newActive;
			changeActiveCounter(mActive ? 1 : -1);
			if (mActive) {
				dispatchingValue(this);
			}
		}
	}

	private class AlwaysActiveObserver extends ObserverWrapper {

		AlwaysActiveObserver(DataObserver<? super T> observer) {
			super(observer);
		}

		@Override
		boolean shouldBeActive() {
			return true;
		}
	}

	static void assertMainThread(String methodName) {
		if (!ArchTaskExecutor.getInstance().isMainThread()) {
			throw new IllegalStateException("Cannot invoke " + methodName + " on a background"
					+ " thread");
		}
	}
}
