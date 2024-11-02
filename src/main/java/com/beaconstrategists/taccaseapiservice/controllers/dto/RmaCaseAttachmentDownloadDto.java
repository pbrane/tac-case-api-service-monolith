package com.beaconstrategists.taccaseapiservice.controllers.dto;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class RmaCaseAttachmentDownloadDto {
    private String name;
    private String mimeType;
    private Resource resource;
}