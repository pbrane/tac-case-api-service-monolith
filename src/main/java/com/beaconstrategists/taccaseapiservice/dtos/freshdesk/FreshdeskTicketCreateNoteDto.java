package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreshdeskTicketCreateNoteDto {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FreshdeskAttachment> attachments = new ArrayList<>();

    private String body;

    private Boolean incoming;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> notify_emails = new ArrayList<>();

    @JsonProperty("private")
    private Boolean privateField;

    private Long userId;
}
