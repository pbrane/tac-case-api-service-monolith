package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RmaCaseAttachmentResponseDto {

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private String version = "1.0.0";

    @NotNull
    private Long id;
    private String name;
    private String description;
    private String mimeType;
    private Float size;
}
