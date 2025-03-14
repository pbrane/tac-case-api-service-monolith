package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshdeskLinksDto {

    @JsonProperty("next")
    private Link next;

    @JsonProperty("count")
    private Link count;

    @JsonProperty("prev")
    private Link prev;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {
        @JsonProperty("href")
        private String href;
    }
}
