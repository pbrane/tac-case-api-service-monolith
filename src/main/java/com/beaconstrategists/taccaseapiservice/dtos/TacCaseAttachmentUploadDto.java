package com.beaconstrategists.taccaseapiservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

//fixme: should be immutable
@Data
public class TacCaseAttachmentUploadDto implements Serializable {

    @Serial
    @JsonIgnore
    @Schema(hidden = true)
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Schema(hidden = true)
    @JsonProperty("version")
    private final String version = "1.0.0";

    //    private Long id;
    private String name;
    private String mimeType;
    private String description;
    private Float size;
    private MultipartFile file; // Include binary data
}
