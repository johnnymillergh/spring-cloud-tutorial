package com.jm.eurekaclientgreetingservice;

import feign.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * <h1>EurekaClientGreetingServiceApplication</h1>
 * <p>Enabled features:</p>
 * <ol>
 * <li>Feign client</li>
 * <li>Service discovery client</li>
 * <li>Circuit breaker</li>
 * <li>Hystrix dashboard</li>
 * </ol>
 * <p>Inspired by:</p>
 * <ul>
 * <li><a href='https://stackoverflow.com/questions/52137023/unable-to-connect-to-command-metric-stream-in-spring-cloud-hystrix-turbine'>Unable to connect to Command Metric Stream in Spring Cloud + Hystrix + Turbine - MIME type (“text/plain”) that is not “text/event-stream”</a>
 * </li>
 * <li><a href='http://appsdeveloperblog.com/hystrix-circuitbreaker-and-feign/'>Hystrix CircuitBreaker and Feign</a>
 * </li>
 * </ul>
 *
 * @author Johnny Miller (锺俊), email: johnnysviva@outlook.com
 * @date 9/4/19 11:57 AM
 **/
@Slf4j
@RestController
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableHystrixDashboard
@SpringBootApplication
public class EurekaClientGreetingServiceApplication {
    private final HelloClient helloClient;

    public EurekaClientGreetingServiceApplication(HelloClient helloClient) {
        this.helloClient = helloClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientGreetingServiceApplication.class, args);
    }

    @Value("${server.port}")
    String port;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam String name) {
        Map<String, Object> param = new HashMap<>(32);
        param.put("id", 123456);
        param.put("name", "This is name");
        log.info("Received result from micro-service: {}", helloClient.hello(param));
        return "Hi, " + name + ", I'm from port: " + port + " " + helloClient.hello(param);
    }

    @FeignClient(name = "eureka-client-feign-service", fallback = HelloClientFallback.class)
    interface HelloClient {
        /**
         * Hello
         *
         * @param param parameter map
         * @return hello sentence
         */
        @RequestMapping(value = "/say-hello", method = POST)
        @Headers("Content-Type: application/json")
        String hello(@RequestParam Map param);
    }

    /**
     * Hello service fallback. Inspired by
     * <a href='http://appsdeveloperblog.com/hystrix-circuitbreaker-and-feign/'>Hystrix CircuitBreaker and Feign</a></p>
     */
    @Component
    static class HelloClientFallback implements HelloClient {
        @Override
        public String hello(Map param) {
            return "Failure to call micro-service";
        }
    }
}
