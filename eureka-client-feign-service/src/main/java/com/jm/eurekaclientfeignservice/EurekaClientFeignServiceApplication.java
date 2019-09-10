package com.jm.eurekaclientfeignservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <h1>EurekaClientFeignServiceApplication</h1>
 * <p>
 * Change description here.
 *
 * @author Johnny Miller (锺俊), email: johnnysviva@outlook.com
 * @date 9/5/19 11:34 AM
 **/
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaClientFeignServiceApplication {
    private final DiscoveryClient client;

    public EurekaClientFeignServiceApplication(DiscoveryClient client) {
        this.client = client;
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientFeignServiceApplication.class, args);
    }

    @RequestMapping("/say-hello")
    public String sayHello(@RequestParam Map param) {
        return "Hello, " + param.get("name") + "!";
    }
}
