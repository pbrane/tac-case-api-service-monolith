package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class TacCaseResponseDto {

    private Long id;

    private String href;

    private String caseNumber;

    private CaseStatus caseStatus;

    private Boolean rmaNeeded;

    @NotNull
    @NotBlank
    private String subject;

    private Integer relatedRmaCount;

    private Integer relatedDispatchCount;

    @NotNull
    @NotBlank
    private String problemDescription;

    private String installationCountry;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime firstResponseDate;

    private String customerTrackingNumber;

    private String contactEmail;

    private String productName;

    private String productSerialNumber;

    private String productFirmwareVersion;

    private String productSoftwareVersion;

    private String caseSolutionDescription;

    private CasePriorityEnum casePriority;

    private String caseOwner;

    private Integer caseNoteCount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseCreatedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseClosedDate;

    private String businessImpact;

    private String accountNumber;

    private String faultySerialNumber;

    private String faultyPartNumber;

    private List<Long> attachmentIds;

    private List<Long> rmaCaseIds;

    private List<Long> noteIds;

}
