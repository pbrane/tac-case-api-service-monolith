package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseCreateDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  //fixme: check on this
public class FreshdeskTacCaseCreateDto extends TacCaseCreateDto {
    private String key;
    private Long ticket;
}
