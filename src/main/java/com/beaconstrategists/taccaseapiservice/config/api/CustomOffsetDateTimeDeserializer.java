package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOffsetDateTimeDeserializer.class);
    //private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final DateTimeFormatter ISO_OFFSET_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();

        if (value == null || value.isEmpty()) {
            logger.warn("Received empty or null date value during deserialization.");
            return null;
        }

        try {
            // Handle date-only input (e.g., "2024-12-06")
            if (value.length() == 10) {
                String adjustedValue = value + "T00:00:00Z";
                logger.info("Received date-only value '{}', converting to full OffsetDateTime '{}'", value, adjustedValue);
                return OffsetDateTime.parse(adjustedValue, ISO_OFFSET_FORMATTER);
            }

            // Handle full ISO OffsetDateTime
            return OffsetDateTime.parse(value, ISO_OFFSET_FORMATTER);

        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse OffsetDateTime from value '{}': {}", value, e.getMessage());
            return null;
        } catch (DateTimeException e) {
            logger.warn("Invalid OffsetDateTime value '{}': {}", value, e.getMessage());
            return null;
        }
    }
}
