package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Source {
    Email(1),
    Portal(2),
    Phone(3),
    Chat(7),
    Feedback_Widget(9),
    Outbound_Email(10);

    private final int value;

    Source(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
