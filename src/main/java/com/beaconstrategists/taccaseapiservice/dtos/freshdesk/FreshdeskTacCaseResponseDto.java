package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
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
