package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreshdeskAttachment {
        private Long id;
        private String name;
        private String contentType;
        private Integer size;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private String attachmentUrl;
}