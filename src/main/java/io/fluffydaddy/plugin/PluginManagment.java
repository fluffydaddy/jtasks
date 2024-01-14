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

import io.fluffydaddy.jreactive.DataObserver;
import io.fluffydaddy.jreactive.DataSubscriber;
import io.fluffydaddy.jreactive.Scheduler;

public interface PluginManagment extends DataSubscriber<DataObserver<Plugin>> {
	void install(Plugin plugin);
	void uninstall(Plugin plugin);
	void clean(Plugin plugin);
	
	void plug(Scheduler scheduler);
	void unplug(Scheduler scheduler);
}
