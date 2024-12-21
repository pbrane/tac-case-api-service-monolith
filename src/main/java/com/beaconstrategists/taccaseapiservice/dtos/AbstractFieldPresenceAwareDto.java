package com.beaconstrategists.taccaseapiservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFieldPresenceAwareDto {

    @JsonIgnore
    @Schema(hidden = true)
    private final Map<String, Boolean> fieldPresence = new HashMap<>();

    public boolean isFieldPresent(String fieldName) {
        return fieldPresence.getOrDefault(fieldName, false);
    }

    public int fieldPresenceCount() {
        return fieldPresence.size();
    }

    protected void markFieldPresent(String fieldName) {
        fieldPresence.put(fieldName, true);
    }
}
