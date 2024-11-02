package com.beaconstrategists.taccaseapiservice.controllers.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TacCaseAttachmentUploadDto {
//    private Long id;
    private String name;
    private String mimeType;
    private String description;
    private Float size;
    private MultipartFile file; // Include binary data
}
