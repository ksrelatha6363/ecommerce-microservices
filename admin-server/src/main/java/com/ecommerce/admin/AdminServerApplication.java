package com.ecommerce.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Boot Admin server. Because it also carries the Eureka client, it
 * auto-discovers every instance registered in the service registry and
 * exposes their health/metrics/env/loggers/threads in one dashboard.
 * <p>
 * Dashboard: http://localhost:8085
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServerApplication.class, args);
    }
}
