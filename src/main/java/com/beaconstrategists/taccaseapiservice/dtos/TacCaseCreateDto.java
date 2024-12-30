package com.beaconstrategists.taccaseapiservice.dtos;

import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = false)
public class TacCaseCreateDto {

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


    @NotNull(message = "Creating a TAC Case requires a non-null subject")
    @NotBlank(message = "Creating a TAC Case requires a non-blank subject")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Subject for the TAC Case, akin to an Email Subject")
    private String subject;

    @NotNull(message = "Creating a TAC Case requires a non-null problemDescription")
    @NotBlank(message = "Creating a TAC Case requires a non-blank problemDescription")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Problem description for the TAC Case, akin to an Email message")
    private String problemDescription;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String installationCountry;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String customerTrackingNumber;

    @NotNull(message = "Creating a TAC Case requires a non-null contactEmail")
    @NotBlank(message = "Creating a TAC Case requires a non-blank contactEmail")
    @Email(message = "The contactEmail must be a valid email address")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Email contact for the TAC Case, akin to a reply-to Email address")
    private String contactEmail;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productName;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productSerialNumber;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productFirmwareVersion;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String productSoftwareVersion;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CasePriorityEnum casePriority;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String businessImpact;

}
