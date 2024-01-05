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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for writing bytes to a ByteQueue.
 * <p>
 * This class extends OutputStream and can be used to write bytes to a ByteQueue. </p>
 */
public class DeviceWriter extends OutputStream {
    /**
     * The Device to write to.
     */
    private final Device device;
    
    /**
     * Creates a new ByteWriter instance.
     *
     * @param device The ByteQueue to write to.
     */
    public DeviceWriter(Device device) {
        this.device = device;
    }
    
    /**
     * Writes a byte to the output stream.
     *
     * @param oneByte The byte to be written.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(int oneByte) throws IOException {
        write(new byte[]{(byte) oneByte}, 0, 1);
    }
    
    /**
     * Writes bytes from an array to the output stream.
     *
     * @param b   The data.
     * @param off The start offset in the data.
     * @param len The number of bytes to write.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(byte @NotNull [] b, int off, int len) throws IOException {
        this.device.write(b, off, len);
    }
}