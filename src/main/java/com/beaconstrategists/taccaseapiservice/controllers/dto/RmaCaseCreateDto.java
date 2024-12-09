package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
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
@JsonIgnoreProperties(ignoreUnknown = false)
public class RmaCaseCreateDto {

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

    @Email(message = "The shipToContactEmail must be a valid email address")
    private String shipToContactEmail;
    private String shipToAttention;
    private String shippedDate;
    private String shippedCarrier;
    private String problemDescription;
    private String installationCountry;
    private String customerTrackingNumber;

    @Email(message = "The contactEmail must be a valid email address")
    private String contactEmail;
}
