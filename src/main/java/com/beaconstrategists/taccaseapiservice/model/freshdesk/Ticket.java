package com.beaconstrategists.taccaseapiservice.model.freshdesk;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
public class Ticket {

    /**
     * Unique ID of the ticket
     */
    private String id;

    /**
     * Ticket attachments. The total size of these attachments cannot exceed 20MB.
     */
    private List<Object> attachments;

    /**
     * Email address added in the 'cc' field of the incoming ticket email
     */
    private List<String> ticketCcEmails;

    /**
     * ID of the company to which this ticket belongs
     */
    private String companyId;

    /**
     * Key value pairs containing the names and values of custom fields.
     */
    private Map<String, Object> customFields;

    /**
     * Set to true if the ticket has been deleted/trashed.
     * Deleted tickets will not be displayed in any views except the "deleted" filter
     */
    private Boolean deleted;

    /**
     * HTML content of the ticket
     */
    private String description;

    /**
     * Content of the ticket in plain text
     */
    private String descriptionText;

    /**
     * Timestamp that denotes when the ticket is due to be resolved
     */
    private OffsetDateTime dueBy;

    /**
     * Email address of the requester.
     * If no contact exists with this email address in Freshdesk,
     * it will be added as a new contact.
     */
    private String email;

    /**
     * ID of email config which is used for this ticket.
     * (i.e., support@yourcompany.com/sales@yourcompany.com)
     */
    private Integer emailConfigId;

    /**
     * Facebook ID of the requester.
     * A contact should exist with this facebook_id in Freshdesk.
     */
    private String facebookId;

    /**
     * Timestamp that denotes when the first response is due
     */
    private OffsetDateTime frDueBy;

    /**
     * Set to true if the ticket has been escalated as the result of first response time being breached
     */
    private Boolean frEscalated;

    /**
     * Email address(e)s added while forwarding a ticket
     */
    private List<String> fwd_emails;

    /**
     * ID of the group to which the ticket has been assigned
     */
    private Integer groupId;

    /**
     * Set to true if the ticket has been escalated for any reason
     * (using the literal name from the API)
     */
    private Boolean isEscalated;

    /**
     * Name of the requester
     */
    private String name;

    /**
     * Phone number of the requester.
     * If no contact exists with this phone number in Freshdesk,
     * it will be added as a new contact.
     * If the phone number is set and the email address is not,
     * then the name attribute is mandatory.
     */
    private String phone;

    /**
     * Priority of the ticket
     */
    private PriorityForTickets priorityForTickets;

    /**
     * ID of the product to which the ticket is associated
     */
    private Integer productId;

    /**
     * Email address added while replying to a ticket
     */
    private List<String> replyCcEmails;

    /**
     * User ID of the requester.
     * For existing contacts, the requester_id can be passed instead of the requester's email.
     */
    private String requesterId;

    /**
     * ID of the agent to whom the ticket has been assigned
     */
    private String responderId;

    /**
     * The channel through which the ticket was created
     */
    private Source source;

    /**
     * Set to true if the ticket has been marked as spam
     */
    private Boolean spam;

    /**
     * Status of the ticket
     */
    private StatusForTickets statusForTickets;

    /**
     * Subject of the ticket
     */
    private String subject;

    /**
     * Tags that have been associated with the ticket
     */
    private List<String> tags;

    /**
     * Email addresses to which the ticket was originally sent
     */
    private List<String> toEmails;

    /**
     * Twitter handle of the requester.
     * If no contact exists with this handle in Freshdesk,
     * it will be added as a new contact.
     */
    private String twitterId;

    /**
     * Helps categorize the ticket according to the different kinds of issues your support team deals with.
     */
    private String type;

    /**
     * Ticket creation timestamp
     */
    private OffsetDateTime createdAt;

    /**
     * Ticket updated timestamp
     */
    private OffsetDateTime updatedAt;

}
