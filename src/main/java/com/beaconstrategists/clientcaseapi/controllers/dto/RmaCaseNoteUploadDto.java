package com.beaconstrategists.clientcaseapi.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RmaCaseNoteUploadDto {

    private String author;
    private OffsetDateTime date;
    private String text;
}
