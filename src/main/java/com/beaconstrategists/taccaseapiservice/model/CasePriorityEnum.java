package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The priorty assigned by TAC to this Case
 */
public enum CasePriorityEnum {
    CRITICAL("Critical"),

    MAJOR("Major"),

    MINOR("Minor");

    private String value;

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
}
