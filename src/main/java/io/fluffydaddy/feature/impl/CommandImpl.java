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

package io.fluffydaddy.feature.impl;

import io.fluffydaddy.jreactive.ErrorObserver;
import io.fluffydaddy.feature.ICommand;
import io.fluffydaddy.feature.IDeploy;
import io.fluffydaddy.feature.IEmploy;

public abstract class CommandImpl<R> implements ICommand<R> {
	@Override
	public IDeploy<R> deploy() {
		return new DeployImpl<>(this::execute);
	}

	@Override
	public IEmploy<R> employ() {
		return new EmployImpl<>(this::destroy);
	}

	protected abstract R execute(ErrorObserver errors);
	protected abstract R destroy(ErrorObserver errors);
}
