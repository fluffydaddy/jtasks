/*
 * Copyright (C) 2024 fluffydaddy
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

package io.fluffydaddy.jtasks.impl;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import io.fluffydaddy.jtasks.core.ITaskService;
import io.fluffydaddy.jtasks.core.ITaskTracker;
import io.fluffydaddy.jtasks.core.TrackState;
import io.fluffydaddy.jtasks.execution.ExecutorFactory;
import io.fluffydaddy.reactive.livedata.LiveData;
import io.fluffydaddy.reactive.livedata.MutableLiveData;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TaskService<R, P> extends ExecutorFactory implements ITaskService<R, P> {
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "Task #" + mCount.getAndIncrement());
        }
    };
    
    private abstract static class WorkerTask<R, P> implements Callable<R> {
        P mParam;
        boolean mIsAlive;
    }
    
    private WorkerTask<R, P> mWorker;
    private FutureTask<R> mFuture;
    private final MutableLiveData<TrackState> mState;
    private final Lock mLocker;
    
    private final AtomicBoolean mCanceled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    
    protected volatile ITaskTracker mTracker;
    private Executor mTaskExecutor;
    private Executor mMainExecutor;
    
    public TaskService() {
        super(sThreadFactory);
        mMainExecutor = ArchTaskExecutor.getMainThreadExecutor();
        
        mState = new MutableLiveData<>();
        mLocker = new ReentrantLock();
    }
    
    private void update() {
        mWorker = new WorkerTask<>() {
            @Override
            public R call() {
                mWorker.mIsAlive = true;
                mTaskInvoked.set(true);
                R result = null;
                try {
                    result = doInBackground(mParam);
                } catch (Exception tr) {
                    mCanceled.set(true);
                    handleException(tr);
                } finally {
                    postResult(result);
                }
                return result;
            }
        };
        mFuture = new FutureTask<>(mWorker) {
            @Override
            public void done() {
                mWorker.mIsAlive = false;
                try {
                    postResultIfNotInvoked(get());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        };
        mTaskExecutor = Executors.newSingleThreadExecutor(this);
    }
    
    @Override
    @MainThread
    public void setMainExecutor(Executor mainExecutor) {
        mMainExecutor = Objects.requireNonNull(mainExecutor, "mainExecutor cannot be null");
    }
    
    @Override
    @MainThread
    public void setTaskExecutor(Executor taskExecutor) {
        mTaskExecutor = Objects.requireNonNull(taskExecutor, "taskExecutor cannot be null");
    }
    
    @Override
    public Executor getMainExecutor() {
        return mMainExecutor;
    }
    
    @Override
    public Executor getTaskExecutor() {
        return mTaskExecutor;
    }
    
    private void postResultIfNotInvoked(R result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }
    
    private void postResult(final R result) {
        final Runnable command = () -> {
            try {
                setState(TrackState.FINISHED);
                if (isCanceled()) {
                    onCanceled(result);
                } else {
                    onComplete(result);
                }
            } catch (Exception e) {
                handleException(e);
            }
        };
        // Как-то надо выполнить задачу command в главном потоке...
        mMainExecutor.execute(command);
    }
    
    @Override
    public boolean isAlive() {
        return mWorker.mIsAlive;
    }
    
    @Override
    public boolean isCanceled() {
        return mCanceled.get();
    }
    
    @Override
    public boolean isComplete() {
        return hasState(TrackState.FINISHED);
    }
    
    @Override
    public boolean setState(TrackState state) {
        mState.postValue(state);
        return true;
    }
    
    @Override
    public boolean hasState(TrackState state) {
        return getActiveState() == state;
    }
    
    @Override
    public LiveData<TrackState> getLiveState() {
        return mState;
    }
    
    @Override
    public TrackState getActiveState() {
        return getLiveState().getValue();
    }
    
    @Override
    public void submit(ITaskTracker tracker) {
        mTracker = tracker;
        mTracker.setState(TrackState.SUBMITTED, this);
    }
    
    @Override
    public void cancel(ITaskTracker tracker) {
        if (tracker == mTracker) {
            tracker.setState(TrackState.UNTRACKED, this);
        }
    }
    
    @Override
    public final void execute(P param) {
        update();
        executeOnExecutor(mTaskExecutor, param);
    }
    
    @Override
    public void execute() {
        update();
        executeOnExecutor(mTaskExecutor, null);
    }
    
    @Override
    public void execute(@NonNull Runnable command) {
        mTaskExecutor.execute(command);
    }
    
    @Override
    public R get() {
        try {
            return mFuture.get();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }
    
    @Override
    public R get(long timeInOut, TimeUnit timeInUnit) {
        try {
            return mFuture.get(timeInOut, timeInUnit);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }
    
    private void executeOnExecutor(Executor exec, P param) {
        setState(TrackState.TRACKING);
        
        onExecute();
        
        mWorker.mParam = param;
        exec.execute(mFuture);
    }
    
    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        try {
            postResultIfNotInvoked(get(timeout, unit));
            mWorker.mIsAlive = false;
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    @Override
    public void destroy() {
        setState(TrackState.TERMINATED);
        
        try {
            onTerminate();
            mCanceled.set(mFuture.cancel(true));
            mWorker.mIsAlive = false;
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    @Override
    public void cancel() {
        setState(TrackState.CANCELED);
        
        try {
            mCanceled.set(mFuture.cancel(false));
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    @Override
    public Lock getLock() {
        return mLocker;
    }
    
    @Override
    public void handleException(Thread thread, Throwable cause) {
        setState(TrackState.EXCEPTED);
        onException(cause);
        
        if (getCrashHandler() != null) {
            getCrashHandler().uncaughtException(thread, cause);
        }
    }
    
    @Override
    public void handleException(Throwable cause) {
        handleException(Thread.currentThread(), cause);
    }
    
    @Override
    public boolean await(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean await(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
            return true;
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }
    
    @Override
    public void onExecute() {
    
    }
    
    @Override
    public void onTerminate() {
    
    }
    
    @Override
    public void onException(Throwable cause) {
        cause.printStackTrace(System.err);
    }
    
    @Override
    public void onCanceled(R result) {
    }
    
    @Override
    public void onComplete(R result) {
    }
    
    public ITaskTracker getTracker() {
        return mTracker;
    }
}
