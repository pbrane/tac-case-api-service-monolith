package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreshdeskTicketNoteResponseDto {

    private String body;
    private String bodyText;
    private Long id;
    private Boolean incoming;

    @JsonProperty("private")
    private Boolean privateField; //fixme: this may be a problem (private is a keyword)

    private Long userId;
    private String supportEmail;
    private Long ticketId;
    private List<String> toEmails;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<FreshdeskAttachment> attachments;

}
