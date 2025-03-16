package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Value("${ESCAPE_HTML_STRINGS:false}")
    private boolean escapeHtmlStrings;

    @Bean
    @Qualifier("camelCaseObjectMapper")
    @Primary
    public ObjectMapper camelCaseObjectMapper() {
        // Default ObjectMapper for camelCase (used globally)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //objectMapper.findAndRegisterModules();
        objectMapper.registerModule(htmlEscapingModule());
        return objectMapper;
    }

    @Bean
    @Qualifier("snakeCaseObjectMapper")
    public ObjectMapper snakeCaseObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Add support for Java 8+ date/time
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure dates are not serialized as arrays
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // Preserve snake_case
        objectMapper.registerModule(htmlEscapingModule());
        return objectMapper;
    }

    @Bean
    public SimpleModule htmlEscapingModule() {
        SimpleModule module = new SimpleModule();

        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(
                    DeserializationConfig config,
                    BeanDescription beanDesc,
                    JsonDeserializer<?> deserializer) {

                if (beanDesc.getBeanClass() == String.class) {
                    return new HtmlEscapingStringDeserializer(escapeHtmlStrings);
                }
                return deserializer;
            }
        });

        return module;
    }

}