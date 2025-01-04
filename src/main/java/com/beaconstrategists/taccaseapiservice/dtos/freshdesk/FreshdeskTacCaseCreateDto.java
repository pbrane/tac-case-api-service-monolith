package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseCreateDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
public class FreshdeskTacCaseCreateDto extends TacCaseCreateDto {
    private String key;
    private Long ticket;
}
