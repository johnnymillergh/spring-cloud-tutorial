package com.jm.eurekaclientgreetingservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>EurekaClientGreetingServiceApplication</h1>
 * <p>
 * Change description here.
 *
 * @author Johnny Miller (锺俊), email: johnnysviva@outlook.com
 * @date 9/4/19 11:57 AM
 **/
@RestController
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientGreetingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientGreetingServiceApplication.class, args);
    }

    @Value("${server.port}")
    String port;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam String name) {
        return "Hi, " + name + ", I'm from port: " + port;
    }
}
