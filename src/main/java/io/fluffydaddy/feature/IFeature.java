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

package io.fluffydaddy.feature;

import java.util.concurrent.Callable;

import io.fluffydaddy.jreactive.DataObserver;
import io.fluffydaddy.jreactive.DataSubscriber;

public interface IFeature<P, R> extends DataSubscriber<DataObserver<R>>, Callable<R> {
	IFeature<P, R> survive(ICommand<R> command);
	IFeature<P, R> dispose(ICommand<R> command);
	IFeature<P, R> extract(boolean allCommand);
}
