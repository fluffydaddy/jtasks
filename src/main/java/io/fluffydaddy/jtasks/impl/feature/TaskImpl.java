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

package io.fluffydaddy.jtasks.impl.feature;

import io.fluffydaddy.reactive.Scheduler;
import io.fluffydaddy.jtasks.factory.Service;
import io.fluffydaddy.jtasks.feature.IFeature;
import io.fluffydaddy.jtasks.feature.ITask;

public abstract class TaskImpl<P, R> implements ITask<P, R>, Service<P, R> {
	@Override
	public IFeature<P, R> schedule(final Scheduler scheduler, P param) {
		return new FeatureImpl<>(this, scheduler, param);
	}
}
