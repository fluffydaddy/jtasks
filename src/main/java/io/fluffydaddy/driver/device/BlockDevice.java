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

package io.fluffydaddy.driver.device;

import io.fluffydaddy.driver.Device;
import io.fluffydaddy.jutils.queue.ByteQueue;
import io.fluffydaddy.jutils.queue.ByteQueueListener;

import java.io.IOException;

public class BlockDevice implements Device {
    private final ByteQueue queue;
    private final ByteQueueListener listener;
    
    public BlockDevice(ByteQueue queue, ByteQueueListener listener) {
        this.queue = queue;
        this.listener = listener;
    }
    
    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (this.listener != null) {
            this.listener.onInputUpdate();
        }
        try {
            return this.queue.read(buf, off, len);
        } catch (InterruptedException e) {
            throw new IOException(e.getCause());
        }
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        try {
            this.queue.write(buf, off, len);
        } catch (InterruptedException e) {
            throw new IOException(e.getCause());
        }
    }
}