/*
 * Copyright © 2024 fluffydaddy
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

package io.fluffydaddy.jtasks.extensions.progress;

import io.fluffydaddy.jtasks.extensions.transfer.TransferTask;
import io.fluffydaddy.jutils.annotation.AnyThread;
import io.fluffydaddy.jutils.annotation.MainThread;
import io.fluffydaddy.jutils.driver.Channel;

public abstract class ProgressTask<R> extends TransferTask<R> {
    public ProgressTask(Channel channel) {
        super(channel);
    }

    /**
     * Освобождение ресурсов после конечного результата.
     */
    public abstract void dispose();

    @AnyThread
    public final void progress(int progress) {
        getMainExecutor().execute(() -> onProgressChanged(progress));
    }

    @Override
    public ProgressTracker getTracker() {
        return (ProgressTracker) super.getTracker();
    }

    @MainThread
    public abstract void onProgressChanged(int progress);
}
