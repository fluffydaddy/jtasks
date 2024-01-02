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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import io.fluffydaddy.jutils.Array;
import io.fluffydaddy.jtasks.core.ITaskService;
import io.fluffydaddy.jtasks.core.ITaskTracker;
import io.fluffydaddy.jtasks.core.TrackState;

public class TaskTracker implements ITaskTracker {
    private final Queue<ITaskService> mTrackQueue;
    private final LinkedList<ITaskService> mTasks;

    private boolean mTracking;
    private String mTrackTag;

    private Thread.UncaughtExceptionHandler mCrashHandler;

    private long mTimeout;
    private TimeUnit mTimeUnit;

    private final Thread.UncaughtExceptionHandler mCrashActionRunnable = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable cause) {
            if (mCrashHandler != null) {
                mCrashHandler.uncaughtException(thread, cause);
            }
        }
    };

    public TaskTracker(String tag) {
        mTrackTag = tag;
        mTrackQueue = new ConcurrentLinkedQueue<>();
        mTasks = new LinkedList<>();
    }

    @Override
    public void track(ITaskService task) {
        mTrackQueue.add(task);
    }

    @Override
    public void untrack(ITaskService task) {
        mTrackQueue.remove(task);
    }

    @Override
    public void startTracking() {
        mTracking = true;

        while (!mTrackQueue.isEmpty()) {
            ITaskService task = mTrackQueue.poll();
            mTasks.add(task);
            try {
                task.submit(this);
                if (getState(task) != TrackState.CANCELED) {
                    startTrack(task);
                }
            } catch (Exception e) {
                task.handleException(e);
            }
        }
    }

    @Override
    public List<ITaskService> stopTracking() {
        Array<ITaskService> result = new Array<>();

        while (!mTasks.isEmpty()) {
            ITaskService task = mTasks.poll();
            task.destroy();
            stopTrack(task);
            result.add(task);
        }

        return result;
    }

    @Override
    public Map<ITaskService, ?> awaitTermination() {
        HashMap<ITaskService, Object> result = new HashMap<>();

        while (!mTasks.isEmpty()) {
            ITaskService task = mTasks.poll();
            final Object lastRet;
            if (mTimeout > 0 && mTimeUnit != null) {
                lastRet = task.get(mTimeout, mTimeUnit);
            } else {
                lastRet = task.get();
            }
            if (getState(task) == TrackState.FINISHED) {
                result.put(task, lastRet);
                stopTrack(task);
            }
        }

        return result;
    }

    private void startTrack(ITaskService task) {
        task.setCrashHandler(mCrashActionRunnable);
    }

    private void stopTrack(ITaskService task) {
        task.setCrashHandler(null);
    }

    @Override
    public boolean setState(TrackState state, ITaskService from) {
        return from != null && from.setState(state);
    }

    @Override
    public boolean hasState(TrackState state, ITaskService task) {
        return task != null && task.hasState(state);
    }

    @Override
    public boolean isTracking() {
        return mTracking;
    }

    @Override
    public TrackState getState(ITaskService from) {
        return from != null ? from.getActiveState() : null;
    }

    /*
     * Здесь могда бытт переменная mHistory<TrackState, Task>, но в настоящее
     * время этот мнтод не поддерживается по иным причинам.
     */
    @Override
    public TrackState[] getStates(ITaskService from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTrackTag(String trackTag) {
        mTrackTag = trackTag;
    }

    @Override
    public String getTrackTag() {
        return mTrackTag;
    }

    @Override
    public Queue<ITaskService> getQueue() {
        return mTrackQueue;
    }

    @Override
    public List<ITaskService> getTasks() {
        return mTasks;
    }

    public void setCrashHandler(Thread.UncaughtExceptionHandler uncaughtCrashHandler) {
        mCrashHandler = uncaughtCrashHandler;
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        mTimeout = timeout;
        mTimeUnit = unit;
    }

    @Override
    public long getTimeout() {
        return mTimeout;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return mTimeUnit;
    }
}
