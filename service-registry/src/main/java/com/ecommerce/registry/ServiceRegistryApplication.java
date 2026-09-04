package com.ecommerce.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Registry.
 * Every other microservice (product, order, gateway, admin) registers itself
 * here so it can be discovered by name instead of a hardcoded host:port.
 * <p>
 * Dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
//what is the role:Java Developer:my role:fresher and explain the project indetail,flow of project,possible 