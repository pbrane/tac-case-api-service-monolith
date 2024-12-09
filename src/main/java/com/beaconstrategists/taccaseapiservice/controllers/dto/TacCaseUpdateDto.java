package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.config.TacCaseUpdateDtoSerializer;
import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = TacCaseUpdateDtoSerializer.class)
public class TacCaseUpdateDto {

    @JsonIgnore
    @Schema(hidden = true)
    private Map<String, Boolean> fieldPresence = new HashMap<>();

    public boolean isFieldPresent(String fieldName) {
        return fieldPresence.getOrDefault(fieldName, false);
    }

    private String href;
    @JsonSetter
    public void setHref(String value) {
        this.caseNumber = value;
        fieldPresence.put("href", true);
    }

    private String caseNumber;
    @JsonSetter
    public void setCaseNumber(String value) {
        this.caseNumber = value;
        fieldPresence.put("caseNumber", true);
    }

    private CaseStatus caseStatus;
    @JsonSetter
    public void setCaseStatus(CaseStatus value) {
        this.caseStatus = value;
        fieldPresence.put("caseStatus", true);
    }

    private Boolean rmaNeeded;
    @JsonSetter
    public void setRmaNeeded(Boolean value) {
        this.rmaNeeded = value;
        fieldPresence.put("rmaNeeded", true);
    }

    private String subject;
    @JsonSetter
    public void setSubject(String value) {
        this.subject = value;
        fieldPresence.put("subject", true);
    }

    private Integer relatedRmaCount;
    @JsonSetter
    public void setRelatedRmaCount(Integer value) {
        this.relatedRmaCount = value;
        fieldPresence.put("relatedRmaCount", true);
    }

    private Integer relatedDispatchCount;
    @JsonSetter
    public void setRelatedDispatchCount(Integer value) {
        this.relatedDispatchCount = value;
        fieldPresence.put("relatedDispatchCount", true);
    }

    private String problemDescription;
    @JsonSetter
    public void setProblemDescription(String value) {
        this.problemDescription = value;
        fieldPresence.put("problemDescription", true);
    }

    private String installationCountry;
    @JsonSetter
    public void setInstallationCountry(String value) {
        this.installationCountry = value;
        fieldPresence.put("installationCountry", true);
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime firstResponseDate;
    @JsonSetter
    public void setFirstResponseDate(OffsetDateTime value) {
        this.firstResponseDate = value;
        fieldPresence.put("firstResponseDate", true);
    }

    private String customerTrackingNumber;
    @JsonSetter
    public void setCustomerTrackingNumber(String value) {
        this.customerTrackingNumber = value;
        fieldPresence.put("customerTrackingNumber", true);
    }

    private String contactEmail;
    @JsonSetter
    public void setContactEmail(String value) {
        this.contactEmail = value;
        fieldPresence.put("contactEmail", true);
    }

    private String productName;
    @JsonSetter
    public void setProductName(String value) {
        this.productName = value;
        fieldPresence.put("productName", true);
    }

    private String productSerialNumber;
    @JsonSetter
    public void setProductSerialNumber(String value) {
        this.productSerialNumber = value;
        fieldPresence.put("productSerialNumber", true);
    }

    private String productFirmwareVersion;
    @JsonSetter
    public void setProductFirmwareVersion(String value) {
        this.productFirmwareVersion = value;
        fieldPresence.put("productFirmwareVersion", true);
    }

    private String productSoftwareVersion;
    @JsonSetter
    public void setProductSoftwareVersion(String value) {
        this.productSoftwareVersion = value;
        fieldPresence.put("productSoftwareVersion", true);
    }

    private String caseSolutionDescription;
    @JsonSetter
    public void setCaseSolutionDescription(String value) {
        this.caseSolutionDescription = value;
        fieldPresence.put("caseSolutionDescription", true);
    }

    private CasePriorityEnum casePriority;
    @JsonSetter
    public void setProductSerialNumber(CasePriorityEnum value) {
        this.casePriority = value;
        fieldPresence.put("casePriority", true);
    }

    private String caseOwner;
    @JsonSetter
    public void setCaseOwner(String value) {
        this.caseOwner = value;
        fieldPresence.put("caseOwner", true);
    }

    //fixme: should be getting rid of this
    private Integer caseNoteCount;
    @JsonSetter
    public void setCaseNoteCount(Integer value) {
        this.caseNoteCount = value;
        fieldPresence.put("caseNoteCount", true);
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseCreatedDate;
    @JsonSetter
    public void setCaseCreatedDate(OffsetDateTime value) {
        this.caseCreatedDate = value;
        fieldPresence.put("caseCreatedDate", true);
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseClosedDate;
    @JsonSetter
    public void setCaseClosedDate(OffsetDateTime value) {
        this.caseClosedDate = value;
        fieldPresence.put("caseClosedDate", true);
    }

    private String businessImpact;
    @JsonSetter
    public void setBusinessImpact(String value) {
        this.businessImpact = value;
        fieldPresence.put("businessImpact", true);
    }

    private String accountNumber;
    @JsonSetter
    public void setAccountNumber(String value) {
        this.accountNumber = value;
        fieldPresence.put("accountNumber", true);
    }

    private String faultySerialNumber;
    @JsonSetter
    public void setFaultySerialNumber(String value) {
        this.faultySerialNumber = value;
        fieldPresence.put("faultySerialNumber", true);
    }

    private String faultyPartNumber;
    @JsonSetter
    public void setFaultyPartNumber(String value) {
        this.faultyPartNumber = value;
        fieldPresence.put("faultyPartNumber", true);
    }

}
