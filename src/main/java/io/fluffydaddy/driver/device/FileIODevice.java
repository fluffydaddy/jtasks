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
import io.fluffydaddy.jutils.queue.ByteQueueListener;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIODevice implements Device {
    private final FileDevice device;
    private final ByteQueueListener listener;
    
    public FileIODevice(FileDescriptor descriptor, ByteQueueListener listener) {
        this.device = new FileDevice(descriptor);
        this.listener = listener;
    }
    
    public FileIODevice(String fileName, ByteQueueListener listener) throws IOException {
        this.device = new FileDevice(fileName);
        this.listener = listener;
    }
    
    public FileIODevice(File file, ByteQueueListener listener) throws IOException {
        this.device = new FileDevice(file);
        this.listener = listener;
    }
    
    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (this.listener != null) {
            this.listener.onInputUpdate();
        }
        return this.device.read(buf, off, len);
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        this.device.write(buf, off, len);
    }
    
    public class FileDevice implements Device {
        
        private FileInputStream fileInputStream;
        private FileOutputStream fileOutputStream;
        
        public FileDevice(FileDescriptor fileDescriptor) {
            this.fileInputStream = new FileInputStream(fileDescriptor);
            this.fileOutputStream = new FileOutputStream(fileDescriptor);
        }
        
        public FileDevice(File file) throws IOException {
            this.fileInputStream = new FileInputStream(file);
            this.fileOutputStream = new FileOutputStream(file);
        }
        
        public FileDevice(String filePath) throws IOException {
            this.fileInputStream = new FileInputStream(filePath);
            this.fileOutputStream = new FileOutputStream(filePath);
        }
        
        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            return fileInputStream.read(buf, off, len);
        }
        
        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            fileOutputStream.write(buf, off, len);
        }
    }
}