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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;

public class WebsiteChannel implements Channel {
    private final URL website;
    
    public WebsiteChannel(URL website) {
        this.website = website;
    }
    
    @Override
    public InputStream openInput() throws IOException {
        return website.openConnection().getInputStream();
    }
    
    @Override
    public OutputStream openOutput() throws IOException {
        return website.openConnection().getOutputStream();
    }
    
    @Override
    public Reader openReader(Charset charset) throws IOException {
        return new InputStreamReader(openInput(), charset);
    }
    
    @Override
    public Writer openWriter(Charset charset) throws IOException {
        return new OutputStreamWriter(openOutput(), charset);
    }
}
