package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAttachmentResponseDto {
     private Long id;
     private String contentType;
     private Long size;
     private String name;
     private String attachmentUrl;
     private OffsetDateTime createdAt;
     private OffsetDateTime updatedAt;
}
