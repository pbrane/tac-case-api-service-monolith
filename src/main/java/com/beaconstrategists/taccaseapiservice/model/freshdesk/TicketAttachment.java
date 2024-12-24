package com.beaconstrategists.taccaseapiservice.model.freshdesk;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@Builder
public class TicketAttachment {
    public Long id;
    public String contentType;
    public Long size;
    public String name;
    public String attachmentUrl;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
}