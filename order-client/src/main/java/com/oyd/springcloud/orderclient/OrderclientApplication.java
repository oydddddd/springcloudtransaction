package com.oyd.springcloud.orderclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class OrderclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderclientApplication.class, args);
    }

}
