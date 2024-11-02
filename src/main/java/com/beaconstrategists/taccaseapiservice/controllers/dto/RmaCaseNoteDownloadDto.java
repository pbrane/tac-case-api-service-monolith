package com.beaconstrategists.taccaseapiservice.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RmaCaseNoteDownloadDto {

    private Long id;
    private Long rmaCaseId;
    private String author;
    private OffsetDateTime date;
    private String text;
}
