package com.beaconstrategists.taccaseapiservice.dtos;

import com.beaconstrategists.taccaseapiservice.config.api.GenericFieldPresenceSnakeCaseJsonSerializer;
import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = false)
public class TacCaseUpdateDto extends AbstractFieldPresenceAwareDto {

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Intended to be an URI to the TAC Case")
    private String href;
    @JsonSetter
    public void setHref(String value) {
        this.caseNumber = value;
        markFieldPresent("href");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Intended to be an additional reference number")
    private String caseNumber;
    @JsonSetter
    public void setCaseNumber(String value) {
        this.caseNumber = value;
        markFieldPresent("caseNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The current status of a TAC Case")
    private CaseStatus caseStatus;
    @JsonSetter
    public void setCaseStatus(CaseStatus value) {
        this.caseStatus = value;
        markFieldPresent("caseStatus");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Indicates that an RMA is needed")
    private Boolean rmaNeeded;
    @JsonSetter
    public void setRmaNeeded(Boolean value) {
        this.rmaNeeded = value;
        markFieldPresent("rmaNeeded");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The subject of this TAC Case, akin to an email subject")
    private String subject;
    @JsonSetter
    public void setSubject(String value) {
        this.subject = value;
        markFieldPresent("subject");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "This is deprecated", deprecated = true)
    private Integer relatedRmaCount;
    @JsonSetter
    public void setRelatedRmaCount(Integer value) {
        this.relatedRmaCount = value;
        markFieldPresent("relatedRmaCount");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "This is deprecated", deprecated = true)
    private Integer relatedDispatchCount;
    @JsonSetter
    public void setRelatedDispatchCount(Integer value) {
        this.relatedDispatchCount = value;
        markFieldPresent("relatedDispatchCount");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The description of this problem, akin to the message of an email")
    private String problemDescription;
    @JsonSetter
    public void setProblemDescription(String value) {
        this.problemDescription = value;
        markFieldPresent("problemDescription");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The country where the product is physically installed")
    private String installationCountry;
    @JsonSetter
    public void setInstallationCountry(String value) {
        this.installationCountry = value;
        markFieldPresent("installationCountry");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A Timestamp indicating the first response to this case")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime firstResponseDate;
    @JsonSetter
    public void setFirstResponseDate(OffsetDateTime value) {
        this.firstResponseDate = value;
        markFieldPresent("firstResponseDate");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A field useful to the customer")
    private String customerTrackingNumber;
    @JsonSetter
    public void setCustomerTrackingNumber(String value) {
        this.customerTrackingNumber = value;
        markFieldPresent("customerTrackingNumber");
    }

    @Email
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The customer email address to contact about this case")
    private String contactEmail;
    @JsonSetter
    public void setContactEmail(String value) {
        this.contactEmail = value;
        markFieldPresent("contactEmail");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The product's name")
    private String productName;
    @JsonSetter
    public void setProductName(String value) {
        this.productName = value;
        markFieldPresent("productName");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The product serial number")
    private String productSerialNumber;
    @JsonSetter
    public void setProductSerialNumber(String value) {
        this.productSerialNumber = value;
        markFieldPresent("productSerialNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The product firmware version")
    private String productFirmwareVersion;
    @JsonSetter
    public void setProductFirmwareVersion(String value) {
        this.productFirmwareVersion = value;
        markFieldPresent("productFirmwareVersion");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The product software version")
    private String productSoftwareVersion;
    @JsonSetter
    public void setProductSoftwareVersion(String value) {
        this.productSoftwareVersion = value;
        markFieldPresent("productSoftwareVersion");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "A detailed description of the solution")
    private String caseSolutionDescription;
    @JsonSetter
    public void setCaseSolutionDescription(String value) {
        this.caseSolutionDescription = value;
        markFieldPresent("caseSolutionDescription");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The Priority of the TAC Case")
    private CasePriorityEnum casePriority;
    @JsonSetter
    public void setCasePriority(CasePriorityEnum value) {
        this.casePriority = value;
        markFieldPresent("casePriority");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The name of the owner of the TAC Case")
    private String caseOwner;
    @JsonSetter
    public void setCaseOwner(String value) {
        this.caseOwner = value;
        markFieldPresent("caseOwner");
    }

    //fixme: should be getting rid of this
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, deprecated = true, description = "This field is deprecated")
    private Integer caseNoteCount;
    @JsonSetter
    public void setCaseNoteCount(Integer value) {
        this.caseNoteCount = value;
        markFieldPresent("caseNoteCount");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The date the TAC Case was created")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseCreatedDate;
    @JsonSetter
    public void setCaseCreatedDate(OffsetDateTime value) {
        this.caseCreatedDate = value;
        markFieldPresent("caseCreatedDate");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The date the TAC Case was closed")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseClosedDate;
    @JsonSetter
    public void setCaseClosedDate(OffsetDateTime value) {
        this.caseClosedDate = value;
        markFieldPresent("caseClosedDate");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The impact to the business determined by the customer")
    private String businessImpact;
    @JsonSetter
    public void setBusinessImpact(String value) {
        this.businessImpact = value;
        markFieldPresent("businessImpact");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The customer's account number")
    private String accountNumber;
    @JsonSetter
    public void setAccountNumber(String value) {
        this.accountNumber = value;
        markFieldPresent("accountNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The serial number of the faulty equipment")
    private String faultySerialNumber;
    @JsonSetter
    public void setFaultySerialNumber(String value) {
        this.faultySerialNumber = value;
        markFieldPresent("faultySerialNumber");
    }

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The part number of the faulty equipment")
    private String faultyPartNumber;
    @JsonSetter
    public void setFaultyPartNumber(String value) {
        this.faultyPartNumber = value;
        markFieldPresent("faultyPartNumber");
    }

}
