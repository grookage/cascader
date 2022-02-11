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

package com.grookage.cascader.core.processors;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.grookage.cascader.core.CascaderConfiguration;
import com.grookage.cascader.core.models.RequestMethod;
import com.grookage.cascader.core.utils.OkhttpUtils;
import com.hystrix.configurator.core.HystrixConfigurationFactory;
import io.appform.core.hystrix.CommandFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.apache.commons.text.StringSubstitutor;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;

@SuppressWarnings("unused")
@Slf4j
@Getter
public class CascaderProcessor {

    private static final String HTTPS = "https";
    private static final String HTTP = "http";

    private final CascaderConfiguration cascaderConfiguration;
    private final OkHttpClient okHttpClient;

    @Builder
    public CascaderProcessor(final CascaderConfiguration cascaderConfiguration,
                             final MetricRegistry metricRegistry){
        this.cascaderConfiguration = cascaderConfiguration;
        this.okHttpClient = OkhttpUtils.getClient(cascaderConfiguration, metricRegistry);
        HystrixConfigurationFactory.init(cascaderConfiguration.getHystrix());
    }

    private String resolvePath(final String path, final UriInfo uriInfo) {
        String uri = null;
        if (Strings.isNullOrEmpty(path)) {
            if (null != uriInfo.getPathParameters()) {
                uri = StringSubstitutor.replace(path, uriInfo.getPathParameters());
            }
        } else {
            uri = path;
        }
        if (Strings.isNullOrEmpty(uri)) {
            uri = path;
        }
        return uri.charAt(0) == '/' ? uri : "/" + uri;
    }

    private HttpUrl url(final String path, final UriInfo uriInfo){
        final var scheme = cascaderConfiguration.getScheme().equalsIgnoreCase(HTTPS) ? HTTPS : HTTP;
        final var builder = new HttpUrl.Builder();
        if (null != uriInfo.getQueryParameters()) {
            uriInfo.getQueryParameters().forEach((key, values) -> values
                    .forEach(value -> builder.addQueryParameter(key, value)));
        }
        builder.host(cascaderConfiguration.getHost());
        if(Objects.equals(scheme, HTTPS)){
            builder.scheme(HTTPS);
            builder.port(443);
        }else{
            builder.scheme(HTTP);
            builder.port(cascaderConfiguration.getPort());
        }
        builder.encodedPath(resolvePath(path, uriInfo));
        return builder.build();
    }

    private Request getRequest(final RequestMethod requestMethod,
                               final HttpHeaders headers,
                               final HttpUrl url,
                               final byte[] body
    ){
        final var contentType = headers != null && headers.getRequestHeaders() != null && !headers.getRequestHeaders().isEmpty() ?
                headers.getRequestHeaders().getFirst(HttpHeaders.CONTENT_TYPE) : null;

        val mediaType = contentType != null ? MediaType.parse(contentType) : MediaType.parse("*/*");
        val httpRequest = new Request.Builder().url(url);

        if(null != headers && null != headers.getRequestHeaders()){
            headers.getRequestHeaders().forEach(
                    (key, values) -> values.forEach(value -> httpRequest.addHeader(key, value)));
        }
        val requestBody = null != body ? body : new byte[0];
        requestMethod.accept(new RequestMethod.RequestMethodVisitor<Void>() {
            @Override
            public Void visitHead() {
                httpRequest.head();
                return null;
            }

            @Override
            public Void visitGet() {
                httpRequest.get();
                return null;
            }

            @Override
            public Void visitPost() {
                httpRequest.post(
                        RequestBody.create(
                                mediaType, requestBody
                        )
                );
                return null;
            }

            @Override
            public Void visitPut() {
                httpRequest.put(
                        RequestBody.create(
                                mediaType, requestBody
                        )
                );
                return null;
            }

            @Override
            public Void visitOptions() {
                httpRequest.method("OPTIONS", null);
                return null;
            }

            @Override
            public Void visitPatch() {
                httpRequest.patch(
                        RequestBody.create(
                                mediaType, requestBody
                        )
                );
                return null;
            }

            @Override
            public Void visitDelete() {
                httpRequest.delete();
                return null;
            }
        });
        return httpRequest.build();
    }

    @SneakyThrows
    public Response process(
            RequestMethod requestMethod,
            String path,
            HttpHeaders httpHeaders,
            UriInfo uriInfo,
            byte[] body
    ){
        Preconditions.checkNotNull(okHttpClient);
        final var request = getRequest(requestMethod, httpHeaders, url(path, uriInfo), body);
        final var name = uriInfo.getBaseUri() != null ? uriInfo.getBaseUri().toString() : "default";
        log.debug("Proxying the request to host {}, url {} with method {}", request.url().host(), request.url().encodedPath(), requestMethod);
        try {
            final var response = CommandFactory.<okhttp3.Response>create(
                    name, "process", null
            ).executor(() -> okHttpClient.newCall(request).execute()).execute();
            final var responseString = OkhttpUtils.bodyString(response);
            return Response
                    .status(response.code())
                    .type(httpHeaders.getMediaType())
                    .entity(responseString)
                    .build();
        } catch (Exception e) {
            log.error("Error in making the call: {} with baseUri {} and methodType {}", e,
                    uriInfo.getBaseUri().toString(), requestMethod);
            throw e;
        }
    }
}
