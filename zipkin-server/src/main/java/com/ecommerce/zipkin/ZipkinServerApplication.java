package com.ecommerce.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin2.server.internal.EnableZipkinServer;

/**
 * Embedded Zipkin distributed tracing server (no separate download/Docker image needed -
 * it's built into this Spring Boot app via the io.zipkin:zipkin-server dependency).
 * <p>
 * All other services report spans here via management.zipkin.tracing.endpoint.
 * UI / API: http://localhost:9411/zipkin
 */
@SpringBootApplication
@EnableZipkinServer
public class ZipkinServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinServerApplication.class, args);
    }
}
