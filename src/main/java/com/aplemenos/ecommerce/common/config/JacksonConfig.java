package com.aplemenos.ecommerce.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes a shared {@link ObjectMapper} bean. In Spring Boot 4 the Jackson
 * autoconfiguration is modular and does not register an ObjectMapper bean here,
 * yet application code (the payment gateways) needs one to parse webhook JSON.
 * {@code findAndRegisterModules} picks up JSR-310 so java.time types work too.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
