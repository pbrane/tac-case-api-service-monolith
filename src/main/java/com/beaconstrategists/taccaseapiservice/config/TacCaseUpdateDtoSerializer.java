package com.beaconstrategists.taccaseapiservice.config;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseUpdateDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.beaconstrategists.taccaseapiservice.config.api.SimpleTypeResolutionContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.lang.reflect.Field;

public class TacCaseUpdateDtoSerializer extends JsonSerializer<TacCaseUpdateDto> {

    private final ObjectMapper mapper;

    public TacCaseUpdateDtoSerializer(@Qualifier("snakeCaseObjectMapper") ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void serialize(TacCaseUpdateDto dto, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();

        // Get the TypeFactory from the ObjectMapper configuration
        TypeFactory typeFactory = mapper.getSerializationConfig().getTypeFactory();
        PropertyNamingStrategy propertyNamingStrategy = mapper.getSerializationConfig().getPropertyNamingStrategy();

        for (Field field : TacCaseUpdateDto.class.getDeclaredFields()) {
            field.setAccessible(true); // Allow access to private fields
            String fieldName = field.getName(); // CamelCase field name

            if (dto.isFieldPresent(fieldName)) {
                // Create a TypeResolutionContext using the TypeFactory
                TypeResolutionContext typeResolutionContext = new SimpleTypeResolutionContext(typeFactory);

                // Create an AnnotatedField for Jackson's naming strategy
                AnnotatedField annotatedField = new AnnotatedField(typeResolutionContext, field, null);

                // Convert to JSON field name using the naming strategy
                String jsonFieldName = propertyNamingStrategy.
                        nameForField(serializers.getConfig(), annotatedField, fieldName);

                try {
                    // Write the JSON field and its value
                    gen.writeObjectField(jsonFieldName, field.get(dto));
                } catch (IllegalAccessException e) {
                    throw new IOException("Failed to access field: " + fieldName, e);
                }
            }
        }

        gen.writeEndObject();
    }
}
