# Cascader [![Build](https://github.com/grookage/cascader/actions/workflows/build.yml/badge.svg)](https://github.com/grookage/cascader/actions/workflows/build.yml)

A small library to act like a proxy in front of any http web server. Particularly helpful when a service need to migrated amongst environments and a quick proxy is needed to front the requests in the interim

> It is not down on any map; true places never are.
> - Moby- Dick, Herman Melville

### Maven Dependency

Work in Progress. Will publish once tests are written. 

### Build instructions
  - Clone the source:

        git clone github.com/grookage/cascader

  - Build

        mvn install

### Tech

* [Dropwizard](https://github.com/dropwizard/dropwizard) - The bundle that got created for the server. 
* [OkhttpClient](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/) - Http client that just works.
* [Hystrix](https://github.com/Netflix/Hystrix) - For fault tolerance

### Example

## Sample Configuration

```
static class SampleConfiguration extends Configuration{

        private CascaderConfiguration cascaderConfiguration;

    }
}

```

## Bundle Inclusion

```
      CascaderBundle<SampleConfiguration> cascaderBundle = new CascaderBundle<SampleConfiguration>() {

                @Override
                public ProxyConfiguration getProxy(SampleConfiguration configuration) {
                    return configuration.getProxyConfiguration();
                }
      };

      bootstrap.addBundle(cascaderBundle);
```

LICENSE
-------

Copyright 2020 Koushik R <rkoushik.14@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


  
