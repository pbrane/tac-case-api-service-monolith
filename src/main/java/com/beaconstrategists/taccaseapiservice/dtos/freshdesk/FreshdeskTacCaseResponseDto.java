package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FreshdeskTacCaseResponseDto extends TacCaseResponseDto {

    /**
     * This field mirrors a must have configuration in Freshdesk, I used the term Key,
     * which is required for this configuration, but is just the name given for the
     * Primary Field that is required for all Freshdesk Custom Objects.
     */
    private String key;
    private Long ticket;

}
