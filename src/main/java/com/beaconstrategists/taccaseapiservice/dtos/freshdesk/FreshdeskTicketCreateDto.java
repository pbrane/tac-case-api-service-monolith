package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.taccaseapiservice.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

//fixme: check this Lombok configuration
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //fixme: I did this trying to fix sending null parent_id.. could probably use the field tracker
public class FreshdeskTicketCreateDto {

    /*
    Name of the requester.
     */
    private String name;

    /*
    User ID of the requester.
    For existing contacts, the requester_id can be passed instead of the requester's email.
     */
     private Long requesterId;

    /*
    email address of requester
     */
    private String email;

    /*
    Facebook ID of the requester. A contact should exist with this facebook_id in Freshdesk.
     */
    private String facebookId;

    /*
    Phone number of the requester.
    If no contact exists with this phone number in Freshdesk, it will be added as a new contact.
    If the phone number is set and the email address is not, then the name attribute is mandatory.
     */
    private String phone;

    /*
    Twitter handle of the requester.
    If no contact exists with this handle in Freshdesk, it will be added as a new contact.
     */
    private String twitterId;

    /*
    Subject of the ticket.
    The default Value is null.
     */
    private String subject;

    /*
    Helps categorize the ticket according to the different kinds of issues your support team deals with.
    The default Value is null.
     */
    private String type;

    /*
    Status of the ticket.
    The default Value is 2.
     */
    private StatusForTickets status;

    /*
    Priority of the ticket.
    The default value is 1.
     */
    private PriorityForTickets priority;

    /*
    HTML content of the ticket.
     */
    private String description;

    /*
    ID of the agent to whom the ticket has been assigned.
     */
    private Long responderId;

    /*
    Timestamp that denotes when the ticket is due to be resolved.
     */
    private OffsetDateTime dueBy;

    /*
    ID of email config which is used for this ticket. (i.e., support@yourcompany.com/sales@yourcompany.com)
    If product_id is given and email_config_id is not given, product's primary email_config_id will be set.
     */
    private Long emailConfigId;

    /*
    Timestamp that denotes when the first response is due.
     */
    private OffsetDateTime frDueBy;

    /*
    ID of the group to which the ticket has been assigned.
    The default value is the ID of the group that is associated with the given email_config_id.
     */
    private Long groupId;

    /*
    ID of the parent ticket that this ticket should be linked to.
    When passing this field, the current ticket actioned upon will be converted to a child ticket.
     */
    private Long parentId;

    /*
    ID of the product to which the ticket is associated.
    It will be ignored if the email_config_id attribute is set in the request.
     */
    private Long ProductId;

    /*
    The channel through which the ticket was created. The default value is 2.
     */
    private Source source;

    /*
    Tags that have been associated with the ticket
     */
    private List<String> tags; //= new ArrayList<>();

    /*
    Company ID of the requester.
    This attribute can only be set if the Multiple Companies feature is enabled (Estate plan and above)
     */
    private Long companyId;

//    /*
//    ID of the internal agent which the ticket should be assigned with.
//     */
//     Long internalAgentId;
//
//    /*
//    ID of the internal group to which the ticket should be assigned with.
//     */
//    private Long internalGroupId;

    /*
    This attribute for tickets can only be set if Custom Objects is enabled
    and a lookup field has been added under ticket fields.

    The value can either be in the form of the display_id (record id)
    or primary_field_value (user defined record value).

//    The default value is display_id.
//     */
//    private String lookupParameter;

}
