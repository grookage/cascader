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
package com.grookage.cascader.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hystrix.configurator.config.HystrixConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CascaderConfiguration {
    private String name = "DEFAULT";
    @NotNull
    @NotEmpty
    private String host;
    @Min(1)
    private int port;
    @NotNull
    @NotEmpty
    private String scheme; //http or https
    private int connections = 10;
    private int idleTimeoutSeconds = 30;
    private int connectionTimeoutMs = 10000;
    private int operationTimeoutMs = 10000;
    @Valid
    private HystrixConfig hystrix = new HystrixConfig();

    @Builder
    public CascaderConfiguration(String host, int port, String scheme){
        this.name = "DEFAULT";
        this.host = host;
        this.port = port;
        this.scheme = scheme;
        this.connections = 10;
        this.idleTimeoutSeconds = 30;
        this.connectionTimeoutMs = 10000;
        this.operationTimeoutMs = 10000;
        this.hystrix = new HystrixConfig();
    }
}
