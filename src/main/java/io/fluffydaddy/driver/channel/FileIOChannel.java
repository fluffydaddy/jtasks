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

package io.fluffydaddy.driver.channel;

import io.fluffydaddy.annotation.NonNull;
import io.fluffydaddy.driver.Channel;
import io.fluffydaddy.jutils.queue.ByteQueueListener;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class FileIOChannel implements Channel {
    private final FileDescriptor device;
    private final File internal;
    private final ByteQueueListener listener;
    
    public FileIOChannel(File internal, ByteQueueListener listener) {
        this.internal = internal;
        this.device = null;
        this.listener = listener;
    }
    
    public FileIOChannel(String fileName, ByteQueueListener listener) {
        this.internal = new File(fileName);
        this.device = null;
        this.listener = listener;
    }
    
    public FileIOChannel(FileDescriptor device, ByteQueueListener listener) {
        this.device = device;
        this.internal = null;
        this.listener = listener;
    }
    
    @Override
    public InputStream openInput() throws IOException {
        if (internal != null) {
            return new FileQueueInputStream(internal, listener);
        }
        return new FileQueueInputStream(device, listener);
    }
    
    @Override
    public OutputStream openOutput() throws IOException {
        if (internal != null) {
            return new FileOutputStream(internal);
        }
        return new FileOutputStream(device);
    }
    
    @Override
    public Reader openReader(Charset charset) throws IOException {
        return new InputStreamReader(openInput(), charset);
    }
    
    @Override
    public Writer openWriter(Charset charset) throws IOException {
        return new OutputStreamWriter(openOutput(), charset);
    }
    
    private static class FileQueueInputStream extends FileInputStream {
        final ByteQueueListener listener;
        
        public FileQueueInputStream(File file, ByteQueueListener listener) throws FileNotFoundException {
            super(file);
            this.listener = listener;
        }
        
        public FileQueueInputStream(FileDescriptor fdObj, ByteQueueListener listener) {
            super(fdObj);
            this.listener = listener;
        }
        
        @Override
        public int read(@NonNull byte[] buf, int off, int len) throws IOException {
            if (this.listener != null) {
                this.listener.onInputUpdate();
            }
            return super.read(buf, off, len);
        }
    }
}
