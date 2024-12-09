package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class TacCaseCreateDto {

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
