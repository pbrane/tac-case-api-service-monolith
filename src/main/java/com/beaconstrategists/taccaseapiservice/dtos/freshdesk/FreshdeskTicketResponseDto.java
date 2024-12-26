package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) //fixme: check if this is what we want here
//fixme: do we really need this @JsonProperty annotations?
public class FreshdeskTicketResponseDto {
     @JsonProperty("cc_emails")
     private List<String> ccEmails;

     @JsonProperty("fwd_emails")
     private List<String> fwdEmails;

     @JsonProperty("reply_cc_emails")
     private List<String> replyCcEmails;

     @JsonProperty("ticket_cc_emails")
     private List<String> ticketCcEmails;

     @JsonProperty("fr_escalated")
     private Boolean frEscalated;

     @JsonProperty("spam")
     private Boolean spam;

     @JsonProperty("email_config_id")
     private Long emailConfigId;

     @JsonProperty("group_id")
     private Long groupId;

     @JsonProperty("priority")
     private PriorityForTickets priorityForTickets;

     @JsonProperty("requester_id")
     private Long requesterId;

     @JsonProperty("responder_id")
     private Long responderId;

     @JsonProperty("source")
     private Source source;

     @JsonProperty("company_id")
     private Long companyId;

     @JsonProperty("status")
     private StatusForTickets statusForTickets;

     @JsonProperty("subject")
     private String subject;

     @JsonProperty("association_type")
     private String associationType;

     @JsonProperty("support_email")
     private String supportEmail;

     @JsonProperty("to_emails")
     private String toEmails;

     @JsonProperty("product_id")
     private Long productId;

     @JsonProperty("id")
     private Long id;

     @JsonProperty("type")
     private String type;

     @JsonProperty("due_by")
     private OffsetDateTime dueBy;

     @JsonProperty("fr_due_by")
     private OffsetDateTime frDueBy;

     @JsonProperty("is_escalated")
     private Boolean isEscalated;

     @JsonProperty("description")
     private String description;

     @JsonProperty("description_text")
     private String descriptionText;

     @JsonProperty("custom_fields")
     private Map<String, Integer> customFields;

     @JsonProperty("created_at")
     private OffsetDateTime createdAt;

     @JsonProperty("updated_at")
     private OffsetDateTime updatedAt;

     @JsonProperty("tags")
     private List<String> tags;

     @JsonProperty("attachments")
     private List<FreshdeskAttachment> attachments;

     @JsonProperty("source_additional_info")
     private String sourceAdditionalInfo;

     @JsonProperty("associated_tickets_list")
     private List<Long> associatedTicketsList;

     @JsonProperty("stats")
     private FreshdeskTicketStats stats;

     @JsonProperty("nr_due_by")
     private OffsetDateTime nrDueBy;

     @JsonProperty("nr_escalated")
     private Boolean nrEscalated;
}
