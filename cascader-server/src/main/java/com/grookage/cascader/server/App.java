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
package com.grookage.cascader.server;

import com.grookage.cascader.core.CascaderConfiguration;
import com.grookage.cascader.server.bundle.CascaderBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class App extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }


    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(new CascaderBundle<>() {
            @Override
            protected CascaderConfiguration withConfiguration(AppConfiguration configuration) {
                return configuration.getCascaderConfiguration();
            }
        });
    }

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) {
        /*
            Nothing to do here.
        */
    }


}
