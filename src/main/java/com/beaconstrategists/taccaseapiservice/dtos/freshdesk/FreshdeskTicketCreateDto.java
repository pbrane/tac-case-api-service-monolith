package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.beaconstrategists.freshdeskapiclient.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.freshdeskapiclient.model.freshdesk.Source;
import com.beaconstrategists.freshdeskapiclient.model.freshdesk.StatusForTickets;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

//fixme: check this Lombok configuration
@Value
@Builder
public class FreshdeskTicketCreateDto {

    /*
    ID
     */
//     Long id;

    /*
    Name of the requester.
     */
     String name;

    /*
    User ID of the requester.
    For existing contacts, the requester_id can be passed instead of the requester's email.
     */
     Long requesterId;

    /*
    email address of requester
     */
     String email;

    /*
    Facebook ID of the requester. A contact should exist with this facebook_id in Freshdesk.
     */
     String facebookId;

    /*
    Phone number of the requester.
    If no contact exists with this phone number in Freshdesk, it will be added as a new contact.
    If the phone number is set and the email address is not, then the name attribute is mandatory.
     */
     String phone;

    /*
    Twitter handle of the requester.
    If no contact exists with this handle in Freshdesk, it will be added as a new contact.
     */
     String twitterId;

//    /*
//    External ID of the requester.
//    If no contact exists with this external ID in Freshdesk, they will be added as a new contact.
//     */
//     String uniqueExternalId;

    /*
    Subject of the ticket.
    The default Value is null.
     */
     String subject;

    /*
    Helps categorize the ticket according to the different kinds of issues your support team deals with.
    The default Value is null.
     */
     String type;

    /*
    Status of the ticket.
    The default Value is 2.
     */
     StatusForTickets status;

    /*
    Priority of the ticket.
    The default value is 1.
     */
     PriorityForTickets priority;

    /*
    HTML content of the ticket.
     */
     String description;

    /*
    ID of the agent to whom the ticket has been assigned.
     */
     Long responderId;

//    /*
//    Ticket attachments.
//    The total size of these attachments cannot exceed 20MB.
//     */
//     List<Object> attachments = new ArrayList<>();

//    /*
//    Email address added in the 'cc' field of the incoming ticket email.
//     */
//    String ccEmails;

//    List<Object> customFields = new ArrayList<>();

    /*
    Timestamp that denotes when the ticket is due to be resolved.
     */
     OffsetDateTime dueBy;

    /*
    ID of email config which is used for this ticket. (i.e., support@yourcompany.com/sales@yourcompany.com)
    If product_id is given and email_config_id is not given, product's primary email_config_id will be set.
     */
     Long emailConfigId;

    /*
    Timestamp that denotes when the first response is due.
     */
     OffsetDateTime frDueBy;

    /*
    ID of the group to which the ticket has been assigned.
    The default value is the ID of the group that is associated with the given email_config_id.
     */
     Long groupId;

//    /*
//    ID of the parent ticket that this ticket should be linked to.
//    When passing this field, the current ticket actioned upon will be converted to a child ticket.
//     */
//     Long parentId;

    /*
    ID of the product to which the ticket is associated.
    It will be ignored if the email_config_id attribute is set in the request.
     */
     Long ProductId;

    /*
    The channel through which the ticket was created. The default value is 2.
     */
     Source source;

//    /*
//    Tags that have been associated with the ticket
//     */
//     List<String> tags; //= new ArrayList<>();

    /*
    Company ID of the requester.
    This attribute can only be set if the Multiple Companies feature is enabled (Estate plan and above)
     */
     Long companyId;

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
