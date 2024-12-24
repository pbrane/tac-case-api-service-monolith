package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

//fixme: check this Lombok configuration
//fixme: should this be serializable
@Value
@Builder
public class TicketAttachmentResponseDto {
     Long id;
     String contentType;
     Long size;
     String name;
     String attachmentUrl;
     OffsetDateTime createdAt;
     OffsetDateTime updatedAt;
}
