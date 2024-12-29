package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum TicketTacCaseStatus {
    Open(2),
    Pending(3),
    Resolved(4),
    Closed(5);

    private final int value;

    TicketTacCaseStatus(int value) {
        this.value = value;
    }

    public static TicketTacCaseStatus fromValue(int value) {
        for (TicketTacCaseStatus status : TicketTacCaseStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    public static TicketTacCaseStatus fromString(String value) {
        for (TicketTacCaseStatus status : TicketTacCaseStatus.values()) {
            if (Objects.equals(status.getName(), value))
                return status;
        }
        return null;
    }

    @JsonValue
    private String getName() {
        return switch (this) {
            case Open -> "Open";
            case Pending -> "Pending";
            case Resolved -> "Resolved";
            case Closed -> "Closed";
            default -> null;
        };
    }
}
