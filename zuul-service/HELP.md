# [Router and Filter: Zuul](https://cloud.spring.io/spring-cloud-netflix/reference/html/#_router_and_filter_zuul)

[TOC]

Routing is an integral part of a microservice architecture. For example, / may be mapped to your web application, /api/users is mapped to the user service and /api/shop is mapped to the shop service. Zuul is a JVM-based router and server-side load balancer from Netflix.

[Netflix uses Zuul](https://www.slideshare.net/MikeyCohen1/edge-architecture-ieee-international-conference-on-cloud-engineering-32240146/27) for the following:

- Authentication

- Insights

- Stress Testing

- Canary Testing

- Dynamic Routing

- Service Migration

- Load Shedding

- Security

- Static Response handling

- Active/Active traffic management

Zuul’s rule engine lets rules and filters be written in essentially any JVM language, with built-in support for Java and Groovy.

> The configuration property `zuul.max.host.connections` has been replaced by two new properties, `zuul.host.maxTotalConnections` and `zuul.host.maxPerRouteConnections`, which default to 200 and 20 respectively.

> The default Hystrix isolation pattern (`ExecutionIsolationStrategy`) for all routes is `SEMAPHORE`. `zuul.ribbonIsolationStrategy` can be changed to `THREAD` if that isolation pattern is preferred.

## How to Include Zuul

To include Zuul in your project, use the starter with a group ID of `org.springframework.cloud` and a artifact ID of `spring-cloud-starter-netflix-zuul`. See the [Spring Cloud Project page](https://projects.spring.io/spring-cloud/) for details on setting up your build system with the current Spring Cloud Release Train.

## Embedded Zuul Reverse Proxy

Spring Cloud has created an embedded Zuul proxy to ease the development of a common use case where a UI application wants to make proxy calls to one or more back end services. This feature is useful for a user interface to proxy to the back end services it requires, avoiding the need to manage CORS and authentication concerns independently for all the back ends.

To enable it, annotate a Spring Boot main class with `@EnableZuulProxy`. Doing so causes local calls to be forwarded to the appropriate service. By convention, a service with an ID of `users` receives requests from the proxy located at `/users` (with the prefix stripped). The proxy uses Ribbon to locate an instance to which to forward through discovery. All requests are executed in a [hystrix command](https://cloud.spring.io/spring-cloud-netflix/reference/html/#hystrix-fallbacks-for-routes), so failures appear in Hystrix metrics. Once the circuit is open, the proxy does not try to contact the service.

> the Zuul starter does not include a discovery client, so, for routes based on service IDs, you need to provide one of those on the classpath as well (Eureka is one choice).

To skip having a service automatically added, set `zuul.ignored-services` to a list of service ID patterns. If a service matches a pattern that is ignored but is also included in the explicitly configured routes map, it is unignored, as shown in the following example:

**application.yml**

```yaml
zuul:
  ignoredServices: '*'
  routes:
    users: /myusers/**
```

In the preceding example, all services are ignored, **except** for `users`.

To augment or change the proxy routes, you can add external configuration, as follows:

**application.yml**

```yaml
zuul:
  routes:
    users: /myusers/**
```

The preceding example means that HTTP calls to `/myusers` get forwarded to the `users` service (for example `/myusers/101` is forwarded to `/101`).

To get more fine-grained control over a route, you can specify the path and the serviceId independently, as follows:

**application.yml**

```yaml
zuul:
  routes:
    users:
      path: /myusers/**
      url: https://example.com/users_service
```

These simple url-routes do not get executed as a `HystrixCommand`, nor do they load-balance multiple URLs with Ribbon. To achieve those goals, you can specify a `serviceId` with a static list of servers, as follows:

**application.yml**

```yaml
zuul:
  routes:
    echo:
      path: /myusers/**
      serviceId: myusers-service
      stripPrefix: true

hystrix:
  command:
    myusers-service:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: ...

myusers-service:
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    listOfServers: https://example1.com,http://example2.com
    ConnectTimeout: 1000
    ReadTimeout: 3000
    MaxTotalHttpConnections: 500
    MaxConnectionsPerHost: 100
```

Another method is specifiying a service-route and configuring a Ribbon client for the `serviceId` (doing so requires disabling Eureka support in Ribbon — see [above for more information](https://cloud.spring.io/spring-cloud-netflix/reference/html/#spring-cloud-ribbon-without-eureka)), as shown in the following example:

**application.yml**

```yaml
zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users

ribbon:
  eureka:
    enabled: false

users:
  ribbon:
    listOfServers: example.com,google.com
```

You can provide a convention between `serviceId` and routes by using `regexmapper`. It uses regular-expression named groups to extract variables from `serviceId` and inject them into a route pattern, as shown in the following example:

**ApplicationConfiguration.java**

```java
@Bean
public PatternServiceRouteMapper serviceRouteMapper() {
    return new PatternServiceRouteMapper(
        "(?<name>^.+)-(?<version>v.+$)",
        "${version}/${name}");
}
```

The preceding example means that a `serviceId` of `myusers-v1` is mapped to route `/v1/myusers/**`. Any regular expression is accepted, but all named groups must be present in both `servicePattern` and `routePattern`. If `servicePattern` does not match a `serviceId`, the default behavior is used. In the preceding example, a `serviceId` of `myusers` is mapped to the "/myusers/**" route (with no version detected). This feature is disabled by default and only applies to discovered services.

To add a prefix to all mappings, set `zuul.prefix` to a value, such as `/api`. By default, the proxy prefix is stripped from the request before the request is forwarded by (you can switch this behavior off with `zuul.stripPrefix=false`). You can also switch off the stripping of the service-specific prefix from individual routes, as shown in the following example:

**application.yml**

```yml
zuul:
  routes:
    users:
      path: /myusers/**
      stripPrefix: false
```

> `zuul.stripPrefix` only applies to the prefix set in `zuul.prefix`. It does not have any effect on prefixes defined within a given route’s `path`.

In the preceding example, requests to `/myusers/101` are forwarded to `/myusers/101` on the `users` service.

The `zuul.routes` entries actually bind to an object of type `ZuulProperties`. If you look at the properties of that object, you can see that it also has a `retryable` flag. Set that flag to `true` to have the Ribbon client automatically retry failed requests. You can also set that flag to `true` when you need to modify the parameters of the retry operations that use the Ribbon client configuration.

By default, the `X-Forwarded-Host` header is added to the forwarded requests. To turn it off, set `zuul.addProxyHeaders = false`. By default, the prefix path is stripped, and the request to the back end picks up a `X-Forwarded-Prefix` header (`/myusers` in the examples shown earlier).

If you set a default route (`/`), an application with `@EnableZuulProxy` could act as a standalone server. For example, `zuul.route.home: /` would route all traffic ("/**") to the "home" service.

If more fine-grained ignoring is needed, you can specify specific patterns to ignore. These patterns are evaluated at the start of the route location process, which means prefixes should be included in the pattern to warrant a match. Ignored patterns span all services and supersede any other route specification. The following example shows how to create ignored patterns:

**application.yml**

```yaml
zuul:
  ignoredPatterns: /**/admin/**
  routes:
    users: /myusers/**
```

The preceding example means that all calls (such as `/myusers/101`) are forwarded to `/101` on the `users` service. However, calls including `/admin/` do not resolve.

> If you need your routes to have their order preserved, you need to use a YAML file, as the ordering is lost when using a properties file. The following example shows such a YAML file:

**application.yml**

```yaml
zuul:
  routes:
    users:
      path: /myusers/**
    legacy:
      path: /**
```

If you were to use a properties file, the `legacy` path might end up in front of the `users` path, rendering the `users` path unreachable.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/maven-plugin/)

### Guides
The following guides illustrate how to use some features concretely:

* [Routing and Filtering](https://spring.io/guides/gs/routing-and-filtering/)

# Spring Cloud Netflix Maintenance Mode

The dependencies listed below are in maintenance mode. We do not recommend adding them to
new projects:

*  Zuul

The decision to move most of the Spring Cloud Netflix projects to maintenance mode was
a response to Netflix not continuing maintenance of many of the libraries that we provided
support for.

Please see [this blog entry](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode)
for more information on maintenance mode and a list of suggested replacements for those
libraries.
