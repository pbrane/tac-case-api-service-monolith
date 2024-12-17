package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CustomOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_OFFSET_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            // Handle date-only input (e.g., "2024-12-06")
            if (value.length() == 10) {
                return OffsetDateTime.parse(value + "T00:00:00Z", ISO_OFFSET_FORMATTER);
            }

            // Handle full ISO OffsetDateTime
            return OffsetDateTime.parse(value, ISO_OFFSET_FORMATTER);

        } catch (Exception e) {
            throw new IOException("Unable to deserialize OffsetDateTime: " + value, e);
        }
    }
}
