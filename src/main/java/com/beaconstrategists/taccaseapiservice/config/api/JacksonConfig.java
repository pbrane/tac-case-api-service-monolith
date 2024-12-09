package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Qualifier("camelCaseObjectMapper")
    @Primary
    public ObjectMapper camelCaseObjectMapper() {
        // Default ObjectMapper for camelCase (used globally)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //objectMapper.findAndRegisterModules();
        return objectMapper;
    }


/*
    @Bean
    @Qualifier("snakeCaseObjectMapper")
    public ObjectMapper snakeCaseObjectMapper() {
        // Configure ObjectMapper for snake_case
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule())
                .findAndRegisterModules();
        return mapper;
    }
*/

    @Bean
    @Qualifier("snakeCaseObjectMapper")
    public ObjectMapper snakeCaseObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Add support for Java 8+ date/time
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure dates are not serialized as arrays
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // Preserve snake_case
        return objectMapper;
    }

}