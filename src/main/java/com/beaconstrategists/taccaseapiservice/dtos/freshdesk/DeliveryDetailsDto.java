package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDetailsDto {

    @JsonProperty("failed_emails")
    private List<String> failedEmails;

    @JsonProperty("pending_emails")
    private List<String> pendingEmails;
}
