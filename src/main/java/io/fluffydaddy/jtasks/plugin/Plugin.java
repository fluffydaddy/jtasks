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

package io.fluffydaddy.jtasks.plugin;

import io.fluffydaddy.reactive.DataSubscriber;

import java.io.IOException;

public interface Plugin extends DataSubscriber<PluginListener> {
    // manage(pre init), inject (dependencies), install(Environment)

	/**
	 * @param state Объект для сохранения состояния при выходе из жизненного цикла.
	 * @throws IOException
	 */
	void backup(PluginState state) throws IOException;

	/**
	 * @param state Объект для сохранения состояния при входе в жизненный цикл, если ранее были какие-то изменения.
	 * @throws IOException
	 */
	void restore(PluginState state) throws IOException;
	
    void manage(); // Перед инициализацией.
	void inject(); // Инекция зависимостей.
	void deploy(); // Установка плагина.
	void delete(); // Удаление плагина.
	void update(); // Обновление планина.
	
	// plug and unplug
}
