package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreshdeskAttachment {
        private Long id;
        private String name;
        private String contentType;
        private Integer size;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private String attachmentUrl;
}