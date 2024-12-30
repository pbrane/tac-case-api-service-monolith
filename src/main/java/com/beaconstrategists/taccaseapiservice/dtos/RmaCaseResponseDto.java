package com.beaconstrategists.taccaseapiservice.dtos;

import com.beaconstrategists.taccaseapiservice.config.api.CustomOffsetDateTimeDeserializer;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RmaCaseResponseDto {

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

    @NotNull
    private Long tacCaseId;

    private String caseNumber;

    private String requestType;

    private String newPartSerialNumber;

    private String faultySerialNumber;

    private String faultyPartNumber;

    private String returnedSerialNumber;

    private String returnedPartNumber;

    private CaseStatus caseStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime caseCreatedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime caseClosedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime newPartShippedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime newPartDeliveredDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime faultyPartShippedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime faultyPartDeliveredDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime failureAnalysisStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime failureAnalysisInProgressDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime failureAnalysisFinishedDate;

    private String shipToStreet1;

    private String shipToProvince;

    private String shipToPostalCode;

    private String shipToPhone;

    private String shipToCountry;

    private String shipToCity;

    private String shipToContactEmail;

    private String shipToAttention;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime shippedDate;

    private String shippedCarrier;

    private String problemDescription;

    private String installationCountry;

    private String customerTrackingNumber;

    private String contactEmail;

    private Integer vendorRmaNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer.class)
    private OffsetDateTime newPartDeliveryDateEta;

    private List<Long> attachmentIds;
}
