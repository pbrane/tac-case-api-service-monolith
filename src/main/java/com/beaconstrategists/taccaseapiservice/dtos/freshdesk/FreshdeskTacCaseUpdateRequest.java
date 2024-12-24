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
public class FreshdeskTacCaseUpdateRequest {

    private String displayId;

    private Integer version;

    //fixme: do we really need this annotation?
    @JsonProperty("data")
    private FreshdeskTacCaseUpdateDto data;

    public FreshdeskTacCaseUpdateRequest(FreshdeskTacCaseUpdateDto data) {
        this.data = data;
    }
}
