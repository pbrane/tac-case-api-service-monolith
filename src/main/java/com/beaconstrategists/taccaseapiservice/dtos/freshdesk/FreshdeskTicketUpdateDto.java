package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.freshdeskapiclient.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.freshdeskapiclient.model.freshdesk.Source;
import com.beaconstrategists.freshdeskapiclient.model.freshdesk.StatusForTickets;
import com.beaconstrategists.taccaseapiservice.dtos.AbstractFieldPresenceAwareDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false) //fixme: do we really want/need this here?
//This doesn't work because the class is from a dependency project
//@JsonSerialize(using = GenericFieldPresenceSnakeCaseJsonSerializer.class)
public class FreshdeskTicketUpdateDto extends AbstractFieldPresenceAwareDto {

    /*
    Name of the requester.
     */
     private String name;
     @JsonSetter
     public void setName(String value) {
         this.name = value;
         markFieldPresent("name");
     }

    /*
    User ID of the requester.
    For existing contacts, the requester_id can be passed instead of the requester's email.
     */
     private Long requesterId;
     @JsonSetter
     public void setRequesterId(Long value) {
         this.requesterId = value;
         markFieldPresent("requesterId");
     }

    /*
    email address of requester
     */
    private String email;
    @JsonSetter
    public void setEmail(String value) {
        this.email = value;
        markFieldPresent("email");
    }

    /*
    Facebook ID of the requester. A contact should exist with this facebook_id in Freshdesk.
     */
    private String facebookId;
    @JsonSetter
    public void setFacebookId(String value) {
        this.facebookId = value;
        markFieldPresent("facebookId");
    }

    /*
    Phone number of the requester.
    If no contact exists with this phone number in Freshdesk, it will be added as a new contact.
    If the phone number is set and the email address is not, then the name attribute is mandatory.
     */
    private String phone;
    @JsonSetter
    public void setPhone(String value) {
        this.phone = value;
        markFieldPresent("phone");
    }

    /*
    Twitter handle of the requester.
    If no contact exists with this handle in Freshdesk, it will be added as a new contact.
     */
    private String twitterId;
    @JsonSetter
    public void setTwitterId(String value) {
        this.twitterId = value;
        markFieldPresent("twitterId");
    }

    /*
    External ID of the requester.
    If no contact exists with this external ID in Freshdesk, they will be added as a new contact.
     */
    private String uniqueExternalId;
    @JsonSetter
    public void setUniqueExternalId(String value) {
        this.uniqueExternalId = value;
        markFieldPresent("uniqueExternalId");
    }

    /*
    Subject of the ticket.
    The default Value is null.
     */
    private String subject;
    @JsonSetter
    public void setSubject(String value) {
        this.subject = value;
        markFieldPresent("subject");
    }

    /*
    Helps categorize the ticket according to the different kinds of issues your support team deals with.
    The default Value is null.
     */
    private String type;
    @JsonSetter
    public void setType(String value) {
        this.type = value;
        markFieldPresent("type");
    }

    /*
    Status of the ticket.
    The default Value is 2.
     */
    private StatusForTickets status;
    @JsonSetter
    public void setStatus(StatusForTickets value) {
        this.status = value;
        markFieldPresent("status");
    }

    /*
    Priority of the ticket.
    The default value is 1.
     */
    private PriorityForTickets priority;
    @JsonSetter
    public void setPriority(PriorityForTickets value) {
        this.priority = value;
        markFieldPresent("priority");
    }

    /*
    HTML content of the ticket.
     */
    private String description;
    @JsonSetter
    public void setDescription(String value) {
        this.description = value;
        markFieldPresent("description");
    }

    /*
    ID of the agent to whom the ticket has been assigned.
     */
    private Long responderId;
    @JsonSetter
    public void setResponderId(Long value) {
        this.responderId = value;
        markFieldPresent("responderId");
    }

//    /*
//    Ticket attachments.
//    The total size of these attachments cannot exceed 20MB.
//     */
//     List<Object> attachments; // = new ArrayList<>();
//
//    /*
//    Email address added in the 'cc' field of the incoming ticket email.
//     */
//    List<String> ccEmails;
//
//    List<Object> customFields; // = new ArrayList<>();

    /*
    Timestamp that denotes when the ticket is due to be resolved.
     */
    private OffsetDateTime dueBy;
    @JsonSetter
    public void setDueBy(OffsetDateTime value) {
        this.dueBy = value;
        markFieldPresent("dueBy");
    }

    /*
    ID of email config which is used for this ticket. (i.e., support@yourcompany.com/sales@yourcompany.com)
    If product_id is given and email_config_id is not given, product's primary email_config_id will be set.
     */
    private Long emailConfigId;
    @JsonSetter
    public void setEmailConfigId(Long value) {
        this.emailConfigId = value;
    }

    /*
    Timestamp that denotes when the first response is due.
     */
    private OffsetDateTime frDueBy;
    @JsonSetter
    public void setFrDueBy(OffsetDateTime value) {
        this.frDueBy = value;
        markFieldPresent("frDueBy");
    }

    /*
    ID of the group to which the ticket has been assigned.
    The default value is the ID of the group that is associated with the given email_config_id.
     */
    private Long groupId;
    @JsonSetter
    public void setGroupId(Long value) {
        this.groupId = value;
        markFieldPresent("groupId");
    }

//    /*
//    ID of the parent ticket that this ticket should be linked to.
//    When passing this field, the current ticket actioned upon will be converted to a child ticket.
//     */
//     Long parentId;

    /*
    ID of the product to which the ticket is associated.
    It will be ignored if the email_config_id attribute is set in the request.
     */
    private Long ProductId;
    @JsonSetter
    public void setProductId(Long value) {
        this.ProductId = value;
        markFieldPresent("ProductId");
    }

    /*
    The channel through which the ticket was created. The default value is 2.
     */
    private Source source;
    @JsonSetter
    public void setSource(Source value) {
        this.source = value;
        markFieldPresent("source");
    }

//    /*
//    Tags that have been associated with the ticket
//     */
//     List<String> tags; //= new ArrayList<>();

    /*
    Company ID of the requester.
    This attribute can only be set if the Multiple Companies feature is enabled (Estate plan and above)
     */
    private Long companyId;
    @JsonSetter
    public void setCompanyId(Long value) {
        this.companyId = value;
        markFieldPresent("companyId");
    }

//    /*
//    ID of the internal agent which the ticket should be assigned with.
//     */
//     Long internalAgentId;
//
//    /*
//    ID of the internal group to which the ticket should be assigned with.
//     */
//     Long internalGroupId;

    /*
    This attribute for tickets can only be set if Custom Objects is enabled
    and a lookup field has been added under ticket fields.

    The value can either be in the form of the display_id (record id)
    or primary_field_value (user defined record value).

//    The default value is display_id.
//     */
//     String lookupParameter;

}
