package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PriorityForTickets {
    Low(1),
    Medium(2),
    High(3),
    Urgent(4);

    private final int value;

    PriorityForTickets(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
