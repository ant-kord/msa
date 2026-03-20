package com.example.order.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary // Mark as primary if multiple mappers might exist
    public JsonMapper jsonMapper() {
        // Build and return your customized JsonMapper instance
        return JsonMapper.builder()
                // Add custom modules, features, etc.
                .findAndAddModules() // Automatically register discovered modules
                .build();
    }
}
