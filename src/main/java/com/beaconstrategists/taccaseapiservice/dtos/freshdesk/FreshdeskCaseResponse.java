package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshdeskCaseResponse<T> {

    public FreshdeskCaseResponse(T data) {
        this.data = data;
    }

    //fixme: do we really need this property annotations?
    @JsonProperty("display_id")
    private String displayId;

    //fixme: figure out why these are longs
    @JsonProperty("created_time")
    private Long createdTime;

    //fixme: figure out why these are longs
    @JsonProperty("updated_time")
    private Long updatedTime;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("data")
    private T data;

}
