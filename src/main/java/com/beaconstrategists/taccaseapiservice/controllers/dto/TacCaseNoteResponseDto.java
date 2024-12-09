package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TacCaseNoteResponseDto {

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private String version = "1.0.0";

    @NotNull
    private Long id;
    private Long tacCaseId;
    private String author;
    private OffsetDateTime date;

}
