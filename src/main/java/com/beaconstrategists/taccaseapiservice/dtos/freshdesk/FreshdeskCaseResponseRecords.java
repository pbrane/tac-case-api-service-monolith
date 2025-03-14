package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshdeskCaseResponseRecords<T> {

    private List<FreshdeskCaseResponse<T>> records;

    @JsonProperty("_links")
    private FreshdeskLinksDto links;  // Add this field to support `_links` in the response

}
