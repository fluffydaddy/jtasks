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

import io.fluffydaddy.driver.Channel;
import io.fluffydaddy.jutils.queue.ByteQueue;
import io.fluffydaddy.jutils.queue.ByteQueueListener;
import io.fluffydaddy.jutils.queue.ByteReader;
import io.fluffydaddy.jutils.queue.ByteWriter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class BlockChannel implements Channel {
    final ByteQueue byteQueue;
    final ByteQueueListener byteQueueListener;
    
    public BlockChannel(ByteQueue byteQueue, ByteQueueListener byteQueueListener) {
        this.byteQueue = byteQueue;
        this.byteQueueListener = byteQueueListener;
    }
    
    @Override
    public InputStream openInput() {
        return new ByteReader(byteQueue, byteQueueListener);
    }
    
    @Override
    public OutputStream openOutput() {
        return new ByteWriter(byteQueue);
    }
    
    @Override
    public Reader openReader(Charset charset) {
        return new InputStreamReader(openInput(), charset);
    }
    
    @Override
    public Writer openWriter(Charset charset) {
        return new OutputStreamWriter(openOutput(), charset);
    }
}