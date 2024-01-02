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

import io.fluffydaddy.jutils.Lazy;
import io.fluffydaddy.reactive.ErrorObserver;
import io.fluffydaddy.jtasks.feature.IEmploy;

public class EmployImpl<R> implements IEmploy<R> {
    private boolean canceled = false;

    final Lazy<ErrorObserver, R> executable;

    public EmployImpl(Lazy<ErrorObserver, R> executable) {
        this.executable = executable;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

    @Override
    public boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public R fire(ErrorObserver errors) {
        return executable.invoke(errors);
    }
}
