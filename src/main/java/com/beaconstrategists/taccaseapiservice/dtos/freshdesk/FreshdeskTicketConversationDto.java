package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.model.freshdesk.FreshdeskConversationSource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshdeskTicketConversationDto {

    private String body;
    private String bodyText;
    private Long id;
    private boolean incoming;
    private boolean isPrivate;
    private Long userId;
    private String supportEmail;
    private FreshdeskConversationSource source;
    private Integer category;
    private List<String> toEmails;
    private String fromEmail;
    private List<String> ccEmails;
    private List<String> bccEmails;
    private Integer emailFailureCount;
    private Integer outgoingFailures;
    private Long threadId;
    private String threadMessageId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastEditedAt;
    private Long lastEditedUserId;
    private List<FreshdeskAttachment> attachments;
    private Long automationId;
    private Long automationTypeId;
    private boolean autoResponse;
    private Long ticketId;
    private String threadingType;
    private DeliveryDetailsDto deliveryDetails;
    private String sourceAdditionalInfo;

}
