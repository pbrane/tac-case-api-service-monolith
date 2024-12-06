package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true) //fixme: really?
public class RmaCaseCreateDto {

//    @NotEmpty
    @NotNull(message = "The tacCaseId field is required.")
    private Long tacCaseId;
    private String requestType;
    private String faultySerialNumber;
    private String faultyPartNumber;
    private String shipToStreet1;
    private String shipToProvince;
    private String shipToPostalCode;
    private String shipToPhone;
    private String shipToCountry;
    private String shipToCity;
    private String shipToContactEmail;
    private String shipToAttention;
    private String shippedDate;
    private String shippedCarrier;
    private String problemDescription;
    private String installationCountry;
    private String customerTrackingNumber;
    private String contactEmail;
}
