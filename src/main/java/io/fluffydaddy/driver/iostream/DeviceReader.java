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

package io.fluffydaddy.driver.iostream;

import io.fluffydaddy.driver.Device;
import io.fluffydaddy.jutils.queue.ByteQueueListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * A class for reading bytes from a ByteQueue.
 * <p>
 * This class extends InputStream and can be used to read bytes from a ByteQueue.
 * It also supports notifying a listener when new data is read from the queue. </p>
 */
public class DeviceReader extends InputStream {
    /**
     * The Device to read from.
     */
    private final Device device;
    
    /**
     * The listener to notify when new data is read.
     */
    private final ByteQueueListener listener;
    
    /**
     * Creates a new ByteReader instance.
     *
     * @param device     The ByteQueue to read from.
     * @param listener  The listener to notify when new data is read.
     */
    public DeviceReader(Device device, ByteQueueListener listener) {
        this.device = device;
        this.listener = listener;
    }
    
    /**
     * Reads a byte from the input stream.
     *
     * @return  The byte read.
     * @throws IOException  If an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        return read(new byte[0], 0, 0);
    }
    
    /**
     * Reads bytes into an array from the input stream.
     *
     * @param b     The buffer into which the data is read.
     * @param off   The start offset in the destination array.
     * @param len   The maximum number of bytes to read.
     * @return      The total number of bytes read into the buffer, or -1 if there is no more data.
     * @throws IOException  If an I/O error occurs.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (listener != null) {
            listener.onInputUpdate();
        }
        
        return device.read(b, off, len);
    }
}
