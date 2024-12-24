package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //fixme: check this is needed or correct
//fixme: figure out why this doesn't serialize the full list of fields if it extends the API's TacCaseResponseDto
public class FreshdeskTacCaseResponseDto {

    /**
     * This field mirrors a must have configuration in Freshdesk, I used the term Key,
     * which is required for this configuration, but is just the name given for the
     * Primary Field that is required for all Freshdesk Custom Objects.
     */
    private String key;

    private Long ticket;

    private String caseNumber;

    private Boolean rmaNeeded;

    private Integer relatedDispatchCount;

    private String problemDescription;

    private String installationCountry;

    private LocalDate firstResponseDate;

    private String customerTrackingNumber;

    private String contactEmail;

    private String productName;

    private String productSerialNumber;

    private String productFirmwareVersion;

    private LocalDate caseCreatedDate;

    private LocalDate caseClosedDate;

    private String caseSolutionDescription;

    private String businessImpact;

    private String accountNumber;

    private String faultySerialNumber;

    private String caseOwner;

}
