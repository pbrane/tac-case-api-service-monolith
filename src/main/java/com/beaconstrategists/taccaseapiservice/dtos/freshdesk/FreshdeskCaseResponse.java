package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //fixme: check this lombok configuration
@AllArgsConstructor
@NoArgsConstructor
public class FreshdeskCaseResponse<T> {

    public FreshdeskCaseResponse(T data) {
        this.data = data;
    }

    //fixme: do we really need this property annotations?
    @JsonProperty("display_id")
    private String displayId;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("updated_time")
    private Long updatedTime;

    @JsonProperty("version")
    private int version;

    @JsonProperty("data")
    private T data;

}
