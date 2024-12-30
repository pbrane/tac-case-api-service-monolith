package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
public class FreshdeskRmaCaseResponseDto extends RmaCaseResponseDto {

    private String key;
    private Long ticket;
    private String tacCase;

}
