package com.beaconstrategists.taccaseapiservice.config.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.freshdesk.FreshdeskTicketUpdateDto;
import com.beaconstrategists.taccaseapiservice.config.api.GenericFieldPresenceSnakeCaseJsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//fixme: We have two Jackson Configuration Classes


@Configuration
public class JacksonSerializerConfig {
    @Bean
    @Qualifier("fieldPresenceSnakeCaseSerializingObjectMapper")
    public ObjectMapper createCustomObjectMapper() {
        ObjectMapper customMapper = new ObjectMapper();
        customMapper.registerModule(new Jdk8Module()); //Jackson will now serialize/deserialize fields of type Optional<T>
        customMapper.registerModule(new JavaTimeModule()); // Add support for Java 8+ date/time
        customMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure dates are not serialized as arrays
        customMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        SimpleModule module = new SimpleModule();
        module.addSerializer(FreshdeskTicketUpdateDto.class,
                new GenericFieldPresenceSnakeCaseJsonSerializer<>(customMapper));
        customMapper.registerModule(module);

        return customMapper;
    }

    @Bean
//    @Primary
    @Qualifier("defaultObjectMapperWithJavaTimeModule")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module()); //Jackson will now serialize/deserialize fields of type Optional<T>
        objectMapper.registerModule(new JavaTimeModule()); // Add support for Java 8+ date/time
        return objectMapper;
    }
}
