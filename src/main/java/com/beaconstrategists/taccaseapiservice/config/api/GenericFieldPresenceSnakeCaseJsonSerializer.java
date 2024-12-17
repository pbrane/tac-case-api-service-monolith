package com.beaconstrategists.taccaseapiservice.config.api;

import com.beaconstrategists.taccaseapiservice.controllers.dto.AbstractFieldPresenceAwareDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class GenericFieldPresenceSnakeCaseJsonSerializer<T> extends JsonSerializer<T> {

    private final ObjectMapper mapper;

    public GenericFieldPresenceSnakeCaseJsonSerializer(@Qualifier("snakeCaseObjectMapper") ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public GenericFieldPresenceSnakeCaseJsonSerializer() {
        //Can't use the bean when this call is used by external libraries
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule()); // Add support for Java 8+ date/time
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure dates are not serialized as arrays
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // Preserve snake_case

        this.mapper = objectMapper;
    }

    @Override
    public void serialize(T dto, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        try {
            // Access the 'fieldPresence' map from the AbstractFieldPresenceAwareDto class
            Field fieldPresenceField = AbstractFieldPresenceAwareDto.class.getDeclaredField("fieldPresence");
            fieldPresenceField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Boolean> fieldPresence = (Map<String, Boolean>) fieldPresenceField.get(dto);

            // Traverse the class hierarchy to get all declared fields
            Class<?> currentClass = dto.getClass();
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true); // Allow access to private fields
                    String fieldName = field.getName(); // CamelCase field name

                    // Check if the field is present using the fieldPresence map
                    if (fieldPresence != null && fieldPresence.getOrDefault(fieldName, false)) {
                        // Create a TypeResolutionContext using the TypeFactory
                        TypeResolutionContext typeResolutionContext = new SimpleTypeResolutionContext(mapper.getTypeFactory());

                        // Create an AnnotatedField for Jackson's naming strategy
                        AnnotatedField annotatedField = new AnnotatedField(typeResolutionContext, field, null);

                        // Convert to JSON field name using the naming strategy
                        String jsonFieldName = mapper.getSerializationConfig()
                                .getPropertyNamingStrategy()
                                .nameForField(serializers.getConfig(), annotatedField, fieldName);

                        // Write the field value to the JSON output
                        gen.writeObjectField(jsonFieldName, field.get(dto));
                    }
                }
                currentClass = currentClass.getSuperclass(); // Move up the class hierarchy
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IOException("Failed to serialize DTO fields", e);
        }

        gen.writeEndObject();
    }

}
