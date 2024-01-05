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

package io.fluffydaddy.plugin;

import io.fluffydaddy.jutils.queue.ByteReader;
import io.fluffydaddy.jutils.queue.ByteWriter;

public interface PluginState {
	ByteWriter writer(int contentLength); // Как буфер(файл например changed.list) записывает все данные в файл, а также передает их куда попало.
	ByteReader reader(int bufferLength); // Как буфер(файл например changed.list) записывает все данные в файл, а также передает их куда попало.
}
