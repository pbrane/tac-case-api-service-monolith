package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FreshdeskConversationSource {
    Reply(0),
    Note(2),
    Tweets (5),
    Survey (6),
    Facebook (7),
    Email (8),
    Phone (9),
    eCommerce (11);

    private final Integer value;

    FreshdeskConversationSource(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    @JsonCreator
    public static FreshdeskConversationSource fromValue(Integer value) {
        for (FreshdeskConversationSource source : values()) {
            if (source.value.equals(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
