package com.oyd.springcloud.proxyzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class ProxyzuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyzuulApplication.class, args);
    }

}
