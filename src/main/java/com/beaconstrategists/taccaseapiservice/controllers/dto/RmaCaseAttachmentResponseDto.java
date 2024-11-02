package com.beaconstrategists.taccaseapiservice.controllers.dto;

import lombok.Data;

@Data
public class RmaCaseAttachmentResponseDto {
    private Long id;
    private String name;
    private String description;
    private String mimeType;
    private Float size;
}
