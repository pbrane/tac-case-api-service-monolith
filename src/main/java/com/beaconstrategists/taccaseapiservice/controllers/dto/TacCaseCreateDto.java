package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) //fixme: really?
public class TacCaseCreateDto {

    @NotEmpty
    private String subject;

    @NotEmpty
    private String problemDescription;

    private String installationCountry;

    private String customerTrackingNumber;

    @NotEmpty
    private String contactEmail;

    private String productName;

    private String productSerialNumber;

    private String productFirmwareVersion;

    private String productSoftwareVersion;

    private CasePriorityEnum casePriority;

    private String businessImpact;

}
