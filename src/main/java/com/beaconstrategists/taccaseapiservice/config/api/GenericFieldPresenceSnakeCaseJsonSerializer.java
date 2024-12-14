package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class GenericFieldPresenceSnakeCaseJsonSerializer<T> extends JsonSerializer<T> {

    private final ObjectMapper mapper;

    public GenericFieldPresenceSnakeCaseJsonSerializer(@Qualifier("snakeCaseObjectMapper") ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void serialize(T dto, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        // Get the TypeFactory and PropertyNamingStrategy from the ObjectMapper
        TypeFactory typeFactory = mapper.getSerializationConfig().getTypeFactory();
        PropertyNamingStrategy propertyNamingStrategy = mapper.getSerializationConfig().getPropertyNamingStrategy();

        try {
            // Use reflection to get the fieldPresence map and isFieldPresent method
            Field fieldPresenceField = dto.getClass().getDeclaredField("fieldPresence");
            fieldPresenceField.setAccessible(true);
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IOException("Failed to access fieldPresence or other fields in DTO", e);
        }

        gen.writeEndObject();
    }
}
