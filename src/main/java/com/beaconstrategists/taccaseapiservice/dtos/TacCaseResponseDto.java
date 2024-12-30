package com.beaconstrategists.taccaseapiservice.dtos;

import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TacCaseResponseDto {

/*
    @Serial
    @JsonIgnore
    @Schema(hidden = true)
    private static final long serialVersionUID = 1L;
*/

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private final String version = "1.0.0";


    @NotNull
    private Long id;

    private String caseNumber;

    private CaseStatus caseStatus;

    private Boolean rmaNeeded;

    private String subject;

    private Integer relatedRmaCount;

    private Integer relatedDispatchCount;

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
