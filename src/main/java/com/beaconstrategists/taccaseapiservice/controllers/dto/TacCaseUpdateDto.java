package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.config.api.GenericSnakeCaseJsonSerializer;
import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
@JsonSerialize(using = GenericSnakeCaseJsonSerializer.class)
public class TacCaseUpdateDto extends AbstractFieldPresenceAwareDto implements Serializable {

    @Serial
    @JsonIgnore
    @Schema(hidden = true)
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private String version = "1.0.0";


    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String href;
    @JsonSetter
    public void setHref(String value) {
        this.caseNumber = value;
        markFieldPresent("href");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String caseNumber;
    @JsonSetter
    public void setCaseNumber(String value) {
        this.caseNumber = value;
        markFieldPresent("caseNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CaseStatus caseStatus;
    @JsonSetter
    public void setCaseStatus(CaseStatus value) {
        this.caseStatus = value;
        markFieldPresent("caseStatus");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean rmaNeeded;
    @JsonSetter
    public void setRmaNeeded(Boolean value) {
        this.rmaNeeded = value;
        markFieldPresent("rmaNeeded");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subject;
    @JsonSetter
    public void setSubject(String value) {
        this.subject = value;
        markFieldPresent("subject");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer relatedRmaCount;
    @JsonSetter
    public void setRelatedRmaCount(Integer value) {
        this.relatedRmaCount = value;
        markFieldPresent("relatedRmaCount");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer relatedDispatchCount;
    @JsonSetter
    public void setRelatedDispatchCount(Integer value) {
        this.relatedDispatchCount = value;
        markFieldPresent("relatedDispatchCount");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String problemDescription;
    @JsonSetter
    public void setProblemDescription(String value) {
        this.problemDescription = value;
        markFieldPresent(problemDescription);
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String installationCountry;
    @JsonSetter
    public void setInstallationCountry(String value) {
        this.installationCountry = value;
        markFieldPresent(installationCountry);
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime firstResponseDate;
    @JsonSetter
    public void setFirstResponseDate(OffsetDateTime value) {
        this.firstResponseDate = value;
        markFieldPresent("firstResponseDate");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String customerTrackingNumber;
    @JsonSetter
    public void setCustomerTrackingNumber(String value) {
        this.customerTrackingNumber = value;
        markFieldPresent("customerTrackingNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String contactEmail;
    @JsonSetter
    public void setContactEmail(String value) {
        this.contactEmail = value;
        markFieldPresent("contactEmail");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productName;
    @JsonSetter
    public void setProductName(String value) {
        this.productName = value;
        markFieldPresent("productName");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productSerialNumber;
    @JsonSetter
    public void setProductSerialNumber(String value) {
        this.productSerialNumber = value;
        markFieldPresent("productSerialNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productFirmwareVersion;
    @JsonSetter
    public void setProductFirmwareVersion(String value) {
        this.productFirmwareVersion = value;
        markFieldPresent("productFirmwareVersion");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productSoftwareVersion;
    @JsonSetter
    public void setProductSoftwareVersion(String value) {
        this.productSoftwareVersion = value;
        markFieldPresent("productSoftwareVersion");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String caseSolutionDescription;
    @JsonSetter
    public void setCaseSolutionDescription(String value) {
        this.caseSolutionDescription = value;
        markFieldPresent("caseSolutionDescription");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CasePriorityEnum casePriority;
    @JsonSetter
    public void setProductSerialNumber(CasePriorityEnum value) {
        this.casePriority = value;
        markFieldPresent("productSerialNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String caseOwner;
    @JsonSetter
    public void setCaseOwner(String value) {
        this.caseOwner = value;
        markFieldPresent("caseOwner");
    }

    //fixme: should be getting rid of this
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer caseNoteCount;
    @JsonSetter
    public void setCaseNoteCount(Integer value) {
        this.caseNoteCount = value;
        markFieldPresent("caseNoteCount");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseCreatedDate;
    @JsonSetter
    public void setCaseCreatedDate(OffsetDateTime value) {
        this.caseCreatedDate = value;
        markFieldPresent("caseCreatedDate");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseClosedDate;
    @JsonSetter
    public void setCaseClosedDate(OffsetDateTime value) {
        this.caseClosedDate = value;
        markFieldPresent("caseClosedDate");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String businessImpact;
    @JsonSetter
    public void setBusinessImpact(String value) {
        this.businessImpact = value;
        markFieldPresent("businessImpact");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String accountNumber;
    @JsonSetter
    public void setAccountNumber(String value) {
        this.accountNumber = value;
        markFieldPresent("accountNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String faultySerialNumber;
    @JsonSetter
    public void setFaultySerialNumber(String value) {
        this.faultySerialNumber = value;
        markFieldPresent("faultySerialNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String faultyPartNumber;
    @JsonSetter
    public void setFaultyPartNumber(String value) {
        this.faultyPartNumber = value;
        markFieldPresent("faultyPartNumber");
    }

}
