package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusForTickets {
    Open(2),
    Pending(3),
    Resolved(4),
    Closed(5);

    private final int value;

    StatusForTickets(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
