package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CaseStatus {

  OPEN("open"),
  ACKNOWLEDGED("acknowledged"),
  REJECTED("rejected"),
  PENDING("pending"),
  HELD("held"),
  INPROGRESS("inprogress"),
  CANCELLED("cancelled"),
  CLOSED("closed"),
  RESOLVED("resolved");

  @JsonValue
  private final String value;

  CaseStatus(String value) {
    this.value = value;
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
}
