package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

//fixme: check this Lombok configuration
//fixme: should these DTOs be serializable?
@Value
@Builder
public class TicketAttachmentUploadDto {
    //    private Long id;
    String name;
    String mimeType;
    String description;
    Float size;
    MultipartFile file; // Include binary data

}
