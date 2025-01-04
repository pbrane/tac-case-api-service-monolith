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

    private String displayId;

    private Long createdTime;

    private Long updatedTime;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("data")
    private T data;

}
