package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PriorityForCustomObjects {
    Low("Low"),
    Medium("Medium"),
    High("High"),
    Urgent("Urgent");

    private final String value;

    PriorityForCustomObjects(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}