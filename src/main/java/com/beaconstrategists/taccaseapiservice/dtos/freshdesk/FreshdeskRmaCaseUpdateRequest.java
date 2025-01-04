package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //fixme: check this Lombok configuration
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreshdeskRmaCaseUpdateRequest {

    private String displayId;

    private Integer version;

    private FreshdeskRmaCaseUpdateDto data;

    public FreshdeskRmaCaseUpdateRequest(FreshdeskRmaCaseUpdateDto data) {
        this.data = data;
    }
}
