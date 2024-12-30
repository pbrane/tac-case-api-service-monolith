package com.beaconstrategists.taccaseapiservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RmaCaseNoteDownloadDto {

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


    private Long id;
    private Long rmaCaseId;
    private String author;
    private OffsetDateTime date;
    private String text;
}
