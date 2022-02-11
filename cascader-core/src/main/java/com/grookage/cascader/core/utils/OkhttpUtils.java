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
package com.grookage.cascader.core.utils;

import com.codahale.metrics.MetricRegistry;
import com.grookage.cascader.core.CascaderConfiguration;
import com.raskasa.metrics.okhttp.InstrumentedOkHttpClients;
import lombok.experimental.UtilityClass;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class OkhttpUtils {

    public static String bodyString(Response response) throws IOException {
        try(final ResponseBody body = response.body()) {
            return null != body ? body.string() : null;
        }
    }

    public static OkHttpClient getClient(final CascaderConfiguration cascaderConfiguration,
                                         final MetricRegistry metricRegistry
    ){
        var connections = cascaderConfiguration.getConnections();
        connections = connections == 0 ? 10 : connections;

        var idleTimeOutSeconds = cascaderConfiguration.getIdleTimeoutSeconds();
        idleTimeOutSeconds = idleTimeOutSeconds == 0 ? 30 : idleTimeOutSeconds;

        var connTimeout = cascaderConfiguration.getConnectionTimeoutMs();
        connTimeout = connTimeout == 0 ? 10000 : connTimeout;

        var opTimeout = cascaderConfiguration.getOperationTimeoutMs();
        opTimeout = opTimeout == 0 ? 10000 : opTimeout;

        final var dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(connections);
        dispatcher.setMaxRequestsPerHost(connections);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(connections, idleTimeOutSeconds, TimeUnit.SECONDS))
                .connectTimeout(connTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(opTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(opTimeout, TimeUnit.MILLISECONDS)
                .dispatcher(dispatcher);

        return (metricRegistry != null)
                ? InstrumentedOkHttpClients.create(metricRegistry, clientBuilder.build(), cascaderConfiguration.getName())
                : clientBuilder.build();
    }
}
