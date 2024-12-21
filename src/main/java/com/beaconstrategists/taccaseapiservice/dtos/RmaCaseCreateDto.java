package com.beaconstrategists.taccaseapiservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class RmaCaseCreateDto implements Serializable {

    @Serial
    @JsonIgnore
    @Schema(hidden = true)
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private final String version = "1.0.0";


    @NotNull(message = "The tacCaseId field is required.")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The ID of parent TAC Case to this RMA")
    private Long tacCaseId;

    @Email(message = "The shipToContactEmail must be a valid email address")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Email address to whom the RMA is sent")
    private String shipToContactEmail;

    @Email(message = "The contactEmail must be a valid email address")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The reply-to email address of this RMA")
    private String contactEmail;

    private String requestType;
    private String faultySerialNumber;
    private String faultyPartNumber;
    private String shipToStreet1;
    private String shipToProvince;
    private String shipToPostalCode;
    private String shipToPhone;
    private String shipToCountry;
    private String shipToCity;
    private String shipToAttention;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime shippedDate;

    private String shippedCarrier;
    private String problemDescription;
    private String installationCountry;
    private String customerTrackingNumber;

}
