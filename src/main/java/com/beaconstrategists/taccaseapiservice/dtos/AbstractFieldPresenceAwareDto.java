package com.beaconstrategists.taccaseapiservice.dtos;

import com.beaconstrategists.taccaseapiservice.config.api.GenericFieldPresenceSnakeCaseJsonSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@JsonSerialize(using = GenericFieldPresenceSnakeCaseJsonSerializer.class) //snake case for Freshdesk
public abstract class AbstractFieldPresenceAwareDto implements Serializable {

    @Serial
    @JsonIgnore
    @Schema(hidden = true)
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private final String version = "1.0.0";


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
