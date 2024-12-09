package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

//fixme: should be immutable
@Data
public class TacCaseAttachmentUploadDto {

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private String version = "1.0.0";

    //    private Long id;
    private String name;
    private String mimeType;
    private String description;
    private Float size;
    private MultipartFile file; // Include binary data
}
