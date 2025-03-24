package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshdeskTicketSearchResponseDto {
    private Integer total;
    private List<FreshdeskTicketResponseDto> results;
}