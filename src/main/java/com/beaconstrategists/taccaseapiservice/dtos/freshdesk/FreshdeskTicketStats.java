package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreshdeskTicketStats {
    private OffsetDateTime agentRespondedAt;
    private OffsetDateTime requesterRespondedAt;
    private OffsetDateTime firstRespondedAt;
    private OffsetDateTime statusUpdatedAt;
    private OffsetDateTime reopenedAt;
    private OffsetDateTime resolvedAt;
    private OffsetDateTime closedAt;
    private OffsetDateTime pendingSince;
}
