# Eureka Server

[TOC]

Service Discovery: Eureka instances can be registered and clients can discover the instances using Spring-managed beans. An embedded Eureka server can be created with declarative Java configuration.

# FAQs of Netflix Eureka

## Run Micro Services on Docker


1. Build container from the image generated before on Portainer.IO

   1. Modify the yml file of every Eureka client service

      ```yaml
      eureka:
        client:
          service-url:
            defaultZone: http://eureka-server:8761/eureka/
      ```

2. Maven `clean`, `valid`, `compile`, `package` (Build project)

3. Run Maven command below to build Docket image of a micro service

   ```shell
   dockerfile:build -f pom.xml
   ```


3. [Optional if done] Set up a network configuration on Portainer.IO for intranet communication
   1. Networks -> Add network
   2. Enter `Name`
   3. Click `Create the network`

## [Standalone Mode](https://cloud.spring.io/spring-cloud-netflix/reference/html/#spring-cloud-eureka-server-standalone-mode)

The combination of the two caches (client and server) and the heartbeats make a standalone Eureka server fairly resilient to failure, as long as there is some sort of monitor or elastic runtime (such as Cloud Foundry) keeping it alive. In standalone mode, you might prefer to switch off the client side behavior so that it does not keep trying and failing to reach its peers. The following example shows how to switch off the client-side behavior:

**application.yml (Standalone Eureka Server)**

```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

## [Peer Awareness](https://cloud.spring.io/spring-cloud-netflix/reference/html/#spring-cloud-eureka-server-peer-awareness)

Eureka can be made even more resilient and available by running multiple instances and asking them to register with each other. In fact, this is the default behavior, so all you need to do to make it work is add a valid `serviceUrl` to a peer, as shown in the following example:

**application.yml (Two Peer Aware Eureka Servers)**

```yaml
---
spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1
  client:
    serviceUrl:
      defaultZone: https://peer2/eureka/

---
spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2
  client:
    serviceUrl:
      defaultZone: https://peer1/eureka/
```

In the preceding example, we have a YAML file that can be used to run the same server on two hosts (`peer1` and `peer2`) by running it in different Spring profiles. You could use this configuration to test the peer awareness on a single host (there is not much value in doing that in production) by manipulating `/etc/hosts` to resolve the host names. In fact, the `eureka.instance.hostname` is not needed if you are running on a machine that knows its own hostname (by default, it is looked up by using `java.net.InetAddress`).

You can add multiple peers to a system, and, as long as they are all connected to each other by at least one edge, they synchronize the registrations amongst themselves. If the peers are physically separated (inside a data center or between multiple data centers), then the system can, in principle, survive “split-brain” type failures. You can add multiple peers to a system, and as long as they are all directly connected to each other, they will synchronize the registrations amongst themselves.

**application.yml (Three Peer Aware Eureka Servers)**

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: https://peer1/eureka/,http://peer2/eureka/,http://peer3/eureka/

---
spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1

---
spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2

---
spring:
  profiles: peer3
eureka:
  instance:
    hostname: peer3
```

## [When to Prefer IP Address](https://cloud.spring.io/spring-cloud-netflix/reference/html/#spring-cloud-eureka-server-prefer-ip-address)

In some cases, it is preferable for Eureka to advertise the IP addresses of services rather than the hostname. Set `eureka.instance.preferIpAddress` to `true` and, when the application registers with eureka, it uses its IP address rather than its hostname.

## [Securing The Eureka Server](https://cloud.spring.io/spring-cloud-netflix/reference/html/#securing-the-eureka-server)

You can secure your Eureka server simply by adding Spring Security to your server’s classpath via `spring-boot-starter-security`. By default when Spring Security is on the classpath it will require that a valid CSRF token be sent with every request to the app. Eureka clients will not generally possess a valid cross site request forgery (CSRF) token you will need to disable this requirement for the `/eureka/**` endpoints. For example:

```java
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }
}
```

A demo Eureka Server can be found in the Spring Cloud Samples [repo](https://github.com/spring-cloud-samples/eureka/tree/Eureka-With-Security).