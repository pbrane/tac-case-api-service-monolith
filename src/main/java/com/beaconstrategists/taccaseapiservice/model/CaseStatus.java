package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CaseStatus {

    Open("Open"),
    Pending("Pending"),
    Closed("Closed"),
    Resolved("Resolved");

    @JsonValue
    private final String value;

    CaseStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CaseStatus fromValue(String value) {
        for (CaseStatus b : CaseStatus.values()) {
            if (b.value.equalsIgnoreCase(value)) { // Case-insensitive matching
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static CaseStatus valueOfEnum(String value) {
        return fromValue(value);
    }

}
