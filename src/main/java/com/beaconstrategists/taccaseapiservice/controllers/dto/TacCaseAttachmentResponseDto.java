package com.beaconstrategists.taccaseapiservice.controllers.dto;

import lombok.Data;

@Data
public class TacCaseAttachmentResponseDto {
    private Long id;
    private String name;
    private String description;
    private String mimeType;
    private Float size;
}
