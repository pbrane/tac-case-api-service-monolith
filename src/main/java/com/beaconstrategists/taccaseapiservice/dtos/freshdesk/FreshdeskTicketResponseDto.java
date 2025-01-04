package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
//fixme: do we really need this @JsonProperty annotations?
public class FreshdeskTicketResponseDto {

     private List<String> ccEmails;

     private List<String> fwdEmails;

     private List<String> replyCcEmails;

     private List<String> ticketCcEmails;

     private Boolean frEscalated;

     private Boolean spam;

     private Long emailConfigId;

     private Long groupId;

     @JsonProperty("priority")
     private PriorityForTickets priorityForTickets;

     private Long requesterId;

     private Long responderId;

     private Source source;

     private Long companyId;

     @JsonProperty("status")
     private StatusForTickets statusForTickets;

     private String subject;

     private String associationType;

     private String supportEmail;

     private String toEmails;

     private Long productId;

     private Long id;

     private String type;

     private OffsetDateTime dueBy;

     private OffsetDateTime frDueBy;

     private Boolean isEscalated;

     private String description;

     private String descriptionText;

     private Map<String, Integer> customFields;

     private OffsetDateTime createdAt;

     private OffsetDateTime updatedAt;

     private List<String> tags;

     private List<FreshdeskAttachment> attachments;

     private String sourceAdditionalInfo;

     private List<Long> associatedTicketsList;

     private FreshdeskTicketStats stats;

     private OffsetDateTime nrDueBy;

     private Boolean nrEscalated;
}
