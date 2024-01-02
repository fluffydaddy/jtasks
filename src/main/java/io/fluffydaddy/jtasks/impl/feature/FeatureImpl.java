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

import java.util.Iterator;

import io.fluffydaddy.jutils.Array;
import io.fluffydaddy.jutils.Unit;
import io.fluffydaddy.reactive.DataObserver;
import io.fluffydaddy.reactive.ErrorObserver;
import io.fluffydaddy.reactive.Reactive;
import io.fluffydaddy.reactive.Scheduler;
import io.fluffydaddy.jtasks.factory.Service;
import io.fluffydaddy.jtasks.feature.ICommand;
import io.fluffydaddy.jtasks.feature.IDeploy;
import io.fluffydaddy.jtasks.feature.IEmploy;
import io.fluffydaddy.jtasks.feature.IFeature;

public class FeatureImpl<P, R> implements IFeature<P, R>, ErrorObserver {
    private final Array<DataObserver<R>> observers = new Array<>();
    private final Array<ICommand<R>> commands = new Array<>();
    private final Array<ErrorObserver> errors = new Array<>();
    private final Array<IDeploy<R>> deploys = new Array<>();

    private final Reactive<IDeploy<R>> deployFeature = command -> {
        if (command.isCanceled()) {
            return;
        }
        deploys.add(command);
        final R result = command.execute(FeatureImpl.this);
        forEach((Unit<DataObserver<R>>) it -> it.onData(result));
        command.cancel();
    };
    private final Reactive<IEmploy<R>> employFeature = command -> {
        if (command.isCanceled()) {
            return;
        }
        final R result = command.fire(FeatureImpl.this);
        forEach((Unit<DataObserver<R>>) it -> it.onData(result));
        command.cancel();
    };
    private final Service<P, R> commandService;
    private final Scheduler commandScheduler;
    private final P commandParam;

    public FeatureImpl(Service<P, R> commandService, Scheduler commandScheduler, P commandParam) {
        this.commandParam = commandParam;
        this.commandService = commandService;
        this.commandScheduler = commandScheduler;
    }

    @Override
    public R call() throws Exception {
        return commandService.start(commandParam);
    }

    @Override
    public void subscribe(DataObserver<R> observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(DataObserver<R> observer) {
        observers.remove(observer);
    }

    @Override
    public void cancel() {
        commands.forEach((Unit<ICommand<R>>) it -> commandScheduler.schedule(employFeature, it.employ()));
        observers.clear();
    }

    @Override
    public void forEach(Unit<? super DataObserver<R>> consumer) {
        observers.forEach(consumer);
    }

    @Override
    public boolean isCanceled() {
        return !observers.isEmpty();
    }

    @Override
    public Iterator<DataObserver<R>> iterator() {
        return observers.iterator();
    }

    @Override
    public FeatureImpl<P, R> survive(ICommand<R> command) {
        commands.add(command);
        return this;
    }

    @Override
    public FeatureImpl<P, R> dispose(ICommand<R> command) {
        commands.remove(command);
        return this;
    }

    @Override
    public IFeature<P, R> extract(boolean allCommand) {
        /*if (commands.isEmpty()) {
            return this;
        }*/
        if (allCommand) {
            commands.forEach((Unit<ICommand<R>>) it -> commandScheduler.schedule(deployFeature, it.deploy()));
        } else {
            commandScheduler.schedule(deployFeature, commands.remove().deploy());
        }
        return this;
    }

    @Override
    public void onError(final Throwable cause) {
        errors.forEach((Unit<ErrorObserver>) it -> it.onError(cause));
    }
}
