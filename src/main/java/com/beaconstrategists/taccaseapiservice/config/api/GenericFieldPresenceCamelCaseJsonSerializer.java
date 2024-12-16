package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class GenericFieldPresenceCamelCaseJsonSerializer<T> extends JsonSerializer<T> {

    private final ObjectMapper mapper;

    public GenericFieldPresenceCamelCaseJsonSerializer(@Qualifier("camelCaseObjectMapper") ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public GenericFieldPresenceCamelCaseJsonSerializer() {
        //Can't use the bean when this call is used by external libraries
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Add support for Java 8+ date/time
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure dates are not serialized as arrays

        mapper = objectMapper;
    }

    @Override
    public void serialize(T dto, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        // Get the TypeFactory and PropertyNamingStrategy from the ObjectMapper
        TypeFactory typeFactory = mapper.getSerializationConfig().getTypeFactory();
        PropertyNamingStrategy propertyNamingStrategy = mapper.getSerializationConfig().getPropertyNamingStrategy();

        gen.writeStartObject();

        try {
            Field fieldPresenceField = null;
            try {
                // Assume the field is always in the immediate superclass
                fieldPresenceField = dto.getClass().getSuperclass().getDeclaredField("fieldPresence");
                fieldPresenceField.setAccessible(true); // Allow access to private fields
            } catch (NoSuchFieldException e) {
                throw new IOException("Failed to access 'fieldPresence' field in the superclass", e);
            }
            @SuppressWarnings("unchecked")
            Map<String, Boolean> fieldPresence = (Map<String, Boolean>) fieldPresenceField.get(dto);

            for (Field field : dto.getClass().getDeclaredFields()) {
                field.setAccessible(true); // Allow access to private fields
                String fieldName = field.getName(); // CamelCase field name

                // Check if the field is present using the fieldPresence map
                if (fieldPresence != null && fieldPresence.getOrDefault(fieldName, false)) {
                    // Create a TypeResolutionContext using the TypeFactory
                    TypeResolutionContext typeResolutionContext = new SimpleTypeResolutionContext(typeFactory);

                    // Create an AnnotatedField for Jackson's naming strategy
                    AnnotatedField annotatedField = new AnnotatedField(typeResolutionContext, field, null);

                    // Convert to JSON field name using the naming strategy
                    String jsonFieldName = propertyNamingStrategy.nameForField(serializers.getConfig(), annotatedField, fieldName);

                    // Write the field value to the JSON output
                    gen.writeObjectField(jsonFieldName, field.get(dto));
                }
            }
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to access fieldPresence or other fields in DTO", e);
        }

        gen.writeEndObject();
    }
}
