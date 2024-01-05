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

package io.fluffydaddy.reactive;

import java.util.Collection;

public class DataPublisherUtils {
    public static <T> void removeObserverFromCopyOnWriteSet(Collection<DataObserver<T>> observers, DataObserver<T> observer) {
        if (observers != null) {
            for (DataObserver<T> candidate : observers) {
                if (candidate.equals(observer)) {
                    // Unsupported by CopyOnWriteArraySet: iterator.remove();
                    observers.remove(candidate);
                } else if (candidate instanceof DelegatingObserver) {
                    DataObserver<T> delegate = candidate;
                    while (delegate instanceof DelegatingObserver) {
                        delegate = ((DelegatingObserver) delegate).getDelegate();
                    }
                    if (delegate == null || delegate.equals(observer)) {
                        // Unsupported by CopyOnWriteArraySet: iterator.remove();
                        observers.remove(candidate);
                    }
                }
            }
        }
    }
}
