package com.beaconstrategists.taccaseapiservice.dtos;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = false)
public class RmaCaseUpdateDto extends AbstractFieldPresenceAwareDto {

    private String caseNumber;
    @JsonSetter
    public void setCaseNumber(String value) {
        this.caseNumber = value;
        markFieldPresent("caseNumber");
    }

    private String requestType;
    @JsonSetter
    public void setRequestType(String value) {
        this.requestType = value;
        markFieldPresent("requestType");
    }

    private String newPartSerialNumber;
    @JsonSetter
    public void setNewPartSerialNumber(String value) {
        this.newPartSerialNumber = value;
        markFieldPresent("newPartSerialNumber");
    }

    private String faultySerialNumber;
    @JsonSetter
    public void setFaultySerialNumber(String value) {
        this.faultySerialNumber = value;
        markFieldPresent("faultySerialNumber");
    }

    private String faultyPartNumber;
    @JsonSetter
    public void setFaultyPartNumber(String value) {
        this.faultyPartNumber = value;
        markFieldPresent("faultyPartNumber");
    }

    private String returnedSerialNumber;
    @JsonSetter
    public void setReturnedSerialNumber(String value) {
        this.returnedSerialNumber = value;
        markFieldPresent("returnedSerialNumber");
    }

    private String returnedPartNumber;
    @JsonSetter
    public void setReturnedPartNumber(String value) {
        this.returnedPartNumber = value;
        markFieldPresent("returnedPartNumber");
    }

    private CaseStatus caseStatus;
    @JsonSetter
    public void setCaseStatus(CaseStatus value) {
        this.caseStatus = value;
        markFieldPresent("caseStatus");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime newPartShippedDate;
    @JsonSetter
    public void setNewPartShippedDate(OffsetDateTime value) {
        this.newPartShippedDate = value;
        markFieldPresent("newPartShippedDate");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime newPartDeliveredDate;
    @JsonSetter
    public void setNewPartDeliveredDate(OffsetDateTime value) {
        this.newPartDeliveredDate = value;
        markFieldPresent("newPartDeliveredDate");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime faultyPartShippedDate;
    @JsonSetter
    public void setFaultyPartShippedDate(OffsetDateTime value) {
        this.faultyPartShippedDate = value;
        markFieldPresent("faultyPartShippedDate");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime faultyPartDeliveredDate;
    @JsonSetter
    public void setFaultyPartDeliveredDate(OffsetDateTime value) {
        this.faultyPartDeliveredDate = value;
        markFieldPresent("faultyPartDeliveredDate");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime failureAnalysisStartDate;
    @JsonSetter
    public void setFailureAnalysisStartDate(OffsetDateTime value) {
        this.failureAnalysisStartDate = value;
        markFieldPresent("failureAnalysisStartDate");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime failureAnalysisInProgressDate;
    @JsonSetter
    public void setFailureAnalysisInProgressDate(OffsetDateTime value) {
        this.failureAnalysisInProgressDate = value;
        markFieldPresent("failureAnalysisInProgressDate");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime failureAnalysisFinishedDate;
    @JsonSetter
    public void setFailureAnalysisFinishedDate(OffsetDateTime value) {
        this.failureAnalysisFinishedDate = value;
        markFieldPresent("failureAnalysisFinishedDate");
    }

    private String shipToStreet1;
    @JsonSetter
    public void setShipToStreet1(String value) {
        this.shipToStreet1 = value;
        markFieldPresent("shipToStreet1");
    }

    private String shipToProvince;
    @JsonSetter
    public void setShipToProvince(String value) {
        this.shipToProvince = value;
        markFieldPresent("shipToProvince");
    }

    private String shipToPostalCode;
    @JsonSetter
    public void setShipToPostalCode(String value) {
        this.shipToPostalCode = value;
        markFieldPresent("shipToPostalCode");
    }

    private String shipToPhone;
    @JsonSetter
    public void setShipToPhone(String value) {
        this.shipToPhone = value;
        markFieldPresent("shipToPhone");
    }

    private String shipToCountry;
    @JsonSetter
    public void setShipToCountry(String value) {
        this.shipToCountry = value;
        markFieldPresent("shipToCountry");
    }

    private String shipToCity;
    @JsonSetter
    public void setShipToCity(String value) {
        this.shipToCity = value;
        markFieldPresent("shipToCity");
    }

    private String shipToContactEmail;
    @JsonSetter
    public void setShipToContactEmail(String value) {
        this.shipToContactEmail = value;
        markFieldPresent("shipToContactEmail");
    }

    private String shipToAttention;
    @JsonSetter
    public void setShipToAttention(String value) {
        this.shipToAttention = value;
        markFieldPresent("shipToAttention");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime shippedDate;
    @JsonSetter
    public void setShippedDate(OffsetDateTime value) {
        this.shippedDate = value;
        markFieldPresent("shippedDate");
    }

    private String shippedCarrier;
    @JsonSetter
    public void setShippedCarrier(String value) {
        this.shippedCarrier = value;
        markFieldPresent("shippedCarrier");
    }

    private String problemDescription;
    @JsonSetter
    public void setProblemDescription(String value) {
        this.problemDescription = value;
        markFieldPresent("problemDescription");
    }

    private String installationCountry;
    @JsonSetter
    public void setInstallationCountry(String value) {
        this.installationCountry = value;
        markFieldPresent("installationCountry");
    }

    private String customerTrackingNumber;
    @JsonSetter
    public void setCustomerTrackingNumber(String value) {
        this.customerTrackingNumber = value;
        markFieldPresent("customerTrackingNumber");
    }

    private String contactEmail;
    @JsonSetter
    public void setContactEmail(String value) {
        this.contactEmail = value;
        markFieldPresent("contactEmail");
    }

    private String vendorRmaNumber;
    @JsonSetter
    public void setVendorRmaNumber(String value) {
        this.vendorRmaNumber = value;
        markFieldPresent("vendorRmaNumber");
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime newPartDeliveryDateEta;
    @JsonSetter
    public void setnewPartDeliveryDateEta(OffsetDateTime value) {
        this.newPartDeliveryDateEta = value;
        markFieldPresent("newPartDeliveryDateEta");
    }

}
