package com.jm.eurekaclientgreetingservice;

import feign.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * <h1>EurekaClientGreetingServiceApplication</h1>
 * <p>
 * Change description here.
 *
 * @author Johnny Miller (锺俊), email: johnnysviva@outlook.com
 * @date 9/4/19 11:57 AM
 **/
@Slf4j
@RestController
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
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

    @FeignClient(name = "eureka-client-feign-service")
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
}
