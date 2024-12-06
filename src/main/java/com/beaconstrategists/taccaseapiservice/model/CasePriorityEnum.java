package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The priority assigned by TAC to this Case
 */
public enum CasePriorityEnum {
//    CRITICAL("Critical"),
//    MAJOR("Major"),
//    MINOR("Minor"),
    Low("Low"),
    Medium("Medium"),
    High("High"),
    Urgent("Urgent");

    private final String value;

    CasePriorityEnum(String value) {
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
    public static CasePriorityEnum fromValue(String value) {
        for (CasePriorityEnum b : CasePriorityEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static CasePriorityEnum valueOfEnum(String value) {
        return fromValue(value);
    }
}
