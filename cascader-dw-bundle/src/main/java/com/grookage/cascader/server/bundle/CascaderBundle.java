/*
 * Copyright 2020 Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grookage.cascader.server.bundle;

import com.codahale.metrics.health.HealthCheck;
import com.grookage.cascader.core.CascaderConfiguration;
import com.grookage.cascader.core.processors.CascaderProcessor;
import com.grookage.cascader.server.bundle.lifecycle.LifecycleSignal;
import com.grookage.cascader.server.bundle.resource.CascaderResource;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@SuppressWarnings("unused")
public abstract class CascaderBundle<U extends Configuration> implements ConfiguredBundle<U> {

    protected abstract CascaderConfiguration withConfiguration(U configuration);

    protected List<HealthCheck> withHealthChecks(U configuration){
        return List.of();
    }

    protected List<LifecycleSignal> withLifecycleSignals(U configuration) {
        return List.of();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        /*
            Nothing to init here!
        */
    }

    @Override
    public void run(U configuration, Environment environment) {
        final var cascaderConfiguration = withConfiguration(configuration);
        final var cascaderProcessor = new CascaderProcessor(cascaderConfiguration, environment.metrics());
        final var cascaderResource = new CascaderResource(cascaderProcessor);
        final var healthChecks = withHealthChecks(configuration);
        final var lifecycleSignals = withLifecycleSignals(configuration);
        if(null != lifecycleSignals && !lifecycleSignals.isEmpty()){
            environment.lifecycle().manage(new Managed() {
                @Override
                public void start() {
                    log.info("Starting the cascader");
                    lifecycleSignals.forEach(LifecycleSignal::start);
                    log.info("Cascader started");
                }

                @Override
                public void stop() {
                    log.info("Stopping the cascader");
                    lifecycleSignals.forEach(LifecycleSignal::stop);
                    log.info("Stopped cascader");
                }
            });
        }
        healthChecks.forEach(healthCheck -> environment.healthChecks().register(healthCheck.getClass().getName(), healthCheck));
        environment.jersey().register(cascaderResource);
    }
}
