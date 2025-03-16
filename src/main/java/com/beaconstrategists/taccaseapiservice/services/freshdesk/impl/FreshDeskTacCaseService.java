package com.beaconstrategists.taccaseapiservice.services.freshdesk.impl;

import com.beaconstrategists.taccaseapiservice.config.freshdesk.RestClientConfig;
import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.dtos.freshdesk.*;
import com.beaconstrategists.taccaseapiservice.exceptions.ResourceNotFoundException;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.FieldPresenceModelMapper;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.GenericModelMapper;
import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.FreshdeskConversationSource;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.beaconstrategists.taccaseapiservice.services.TacCaseService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.CompanyService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.RequesterResponderService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.SchemaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("FreshdeskTacCaseService")
public class FreshDeskTacCaseService implements TacCaseService {

    private final RestClientConfig restClientConfig;
    private final RestClient snakeCaseRestClient;

    /*
     * The following RestClient is only needed for updating Tickets
     * Freshdesk Tickets can handle partial updates.
     * Freshdesk Custom Objects (TAC/RMA Cases) require full updates.
     * The updates to Custom Objects are managed with a FieldPresence ModelMapper.
     * So, they don't need, and shouldn't use, this RestClient
     */
    private final RestClient fieldPresenseRestClient;
    private final GenericModelMapper genericModelMapper;

    //fixme: consider having one variable to control these
    @Value("${ESCAPE_HTML_SUBJECT:true}")
    private boolean escapeHtmlSubject;

    @Value("${ESCAPE_HTML_DESCRIPTION:true}")
    private boolean escapeHtmlDescription;

    @Value("${ESCAPE_HTML_NOTES:true}")
    private boolean escapeHtmlNotes;

    //this value is used by the deserializer, if this is true, the other escapes can be ignored
    @Value("${ESCAPE_HTML_STRINGS:false}")
    private boolean escapeHtmlStrings;

    private final SchemaService schemaService;
    private final RequesterResponderService requesterResponderService;
    private final CompanyService companyService;

    public FreshDeskTacCaseService(RestClientConfig restClientConfig,
                                   @Qualifier("snakeCaseRestClient") RestClient snakeCaseRestClient,
                                   SchemaService schemaService,
                                   RequesterResponderService requesterResponderService,
                                   GenericModelMapper genericModelMapper,
                                   @Qualifier("fieldPresenceSnakeCaseSerializingRestClient") RestClient fieldPresenseRestClient, CompanyService companyService) {
        this.restClientConfig = restClientConfig;
        this.snakeCaseRestClient = snakeCaseRestClient;
        this.genericModelMapper = genericModelMapper;
        this.schemaService = schemaService;
        this.requesterResponderService = requesterResponderService;
        this.fieldPresenseRestClient = fieldPresenseRestClient;
        this.companyService = companyService;
    }


    @Override
    public List<TacCaseResponseDto> listTacCases(OffsetDateTime caseCreateDateFrom,
                                                 OffsetDateTime caseCreateDateTo,
                                                 OffsetDateTime caseCreateDateSince,
                                                 List<CaseStatus> caseStatus,
                                                 String logic,
                                                 Integer pageSize,
                                                 Integer pageLimit) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String schemaId = schemaService.getTacCaseSchemaId();

        //Mutating this client to adapt to path creation due to using the UriComponents builder
        //This forces a trailing "/"
        RestClient restClient = snakeCaseRestClient.mutate()
                .baseUrl(restClientConfig.getFreshdeskBaseUri().endsWith("/")
                        ? restClientConfig.getFreshdeskBaseUri()
                        : restClientConfig.getFreshdeskBaseUri() + "/") // Ensure trailing "/"
                .build();

        // Build the base query parameters
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.queryParam("page_size", pageSize);

        if (caseCreateDateFrom != null && caseCreateDateTo != null) {
            uriComponentsBuilder.queryParam("created_time[gte]", caseCreateDateFrom.format(formatter))
                    .queryParam("created_time[lt]", caseCreateDateTo.format(formatter));
        } else if (caseCreateDateSince != null) {
            uriComponentsBuilder.queryParam("created_time[gte]", caseCreateDateSince.format(formatter));
        }

        //uriComponentsBuilder.queryParam("logic", logic);
        uriComponentsBuilder.queryParam("sort_by", "created_time");
        uriComponentsBuilder.queryParam("DESC");

        // Initialize result list
        List<TacCaseResponseDto> tacCaseResponseDtos = new ArrayList<>();

        // Construct the initial API URL
        String query = uriComponentsBuilder.build().getQuery();
        String nextPageUrl = "custom_objects/schemas/" + schemaId + "/records?" + query;

        int pageCount = 0;
        do {
            pageCount++;
            FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> responseRecords = restClient
                    .get()
                    .uri(nextPageUrl)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (responseRecords == null || responseRecords.getRecords() == null) {
                break; // No more records
            }

            // Convert and add records
            List<TacCaseResponseDto> currentBatch = responseRecords.getRecords().stream()
                    .map(this::mapToTacCaseResponseDto)
                    .toList();
            tacCaseResponseDtos.addAll(currentBatch);

            // Get the 'next' page link if available
            FreshdeskLinksDto links = responseRecords.getLinks();
            FreshdeskLinksDto.Link next = links.getNext();
            if (links != null && next != null) {
                nextPageUrl = "custom_objects/" + (next.getHref().startsWith("/") ? next.getHref().substring(1) : next.getHref());
                //nextPageUrl = next.getHref().startsWith("/") ? next.getHref().substring(1) : next.getHref();

            } else {
                nextPageUrl = null; // Stop paging
            }

        } while (nextPageUrl != null && pageCount < pageLimit);

        // Apply filtering logic (if needed)
        if (caseStatus != null && !caseStatus.isEmpty() && "and".equalsIgnoreCase(logic)) {
            tacCaseResponseDtos.removeIf(tacCaseResponseDto -> !caseStatus.contains(tacCaseResponseDto.getCaseStatus()));
        }

        return tacCaseResponseDtos;
    }



    /*
     * Create a TAC Case in Freshdesk
     */
    @Override
    public TacCaseResponseDto create(TacCaseCreateDto tacCaseCreateDto) {

        /*
         * A little housekeeping
         */
        String subject = tacCaseCreateDto.getSubject();
        if (escapeHtmlSubject && !escapeHtmlStrings && subject != null) {
            tacCaseCreateDto.setSubject(HtmlUtils.htmlEscape(subject));
        }

        String problemDescription = tacCaseCreateDto.getProblemDescription();
        if (escapeHtmlDescription && !escapeHtmlStrings && problemDescription != null) {
            tacCaseCreateDto.setProblemDescription(HtmlUtils.htmlEscape(problemDescription));
        }

        /*
         * First create the ticket
         */
        FreshdeskTicketCreateDto freshdeskTicketCreateDto = buildCreateTicketDto(tacCaseCreateDto, requesterResponderService.getResponderId(),
                requesterResponderService.getRequesterId(), companyService.getRequiredCompanyId());
        FreshdeskTicketResponseDto createTicketResponseDto = createFreshdeskTicket(freshdeskTicketCreateDto);

        assert createTicketResponseDto != null;  //fixme: what happens here if null

        /*
         * Next create the TAC Case
         */
        FreshdeskTacCaseCreateDto freshdeskTacCaseCreateDto = genericModelMapper.map(tacCaseCreateDto, FreshdeskTacCaseCreateDto.class);
        freshdeskTacCaseCreateDto.setKey("TAC:" + createTicketResponseDto.getId() + "; " + tacCaseCreateDto.getSubject());
        freshdeskTacCaseCreateDto.setTicket(createTicketResponseDto.getId());
        //fixme: FreshdeskDataCreateRequest and FreshdeskTacCaseRequest are identical
        FreshdeskDataCreateRequest<FreshdeskTacCaseCreateDto> freshdeskTacCaseCreateRequest = new FreshdeskDataCreateRequest<>(freshdeskTacCaseCreateDto);
        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse = createFreshdeskTacCase(freshdeskTacCaseCreateRequest);

        /*
         * Return TAC Case
         * fixme: A bit of code duplication here mapping Freshdesk response to API response
         */
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = freshdeskTacCaseResponse.getData();
        TacCaseResponseDto tacCaseResponseDto = genericModelMapper.map(freshdeskTacCaseResponseDto, TacCaseResponseDto.class);
        tacCaseResponseDto.setId(createTicketResponseDto.getId());
        tacCaseResponseDto.setCaseNumber(freshdeskTacCaseResponse.getDisplayId());
        tacCaseResponseDto.setSubject(createTicketResponseDto.getSubject());
//        tacCaseResponseDto.setCaseOwner(requesterResponderService.getResponderName());
        tacCaseResponseDto.setProblemDescription(createTicketResponseDto.getDescriptionText());
        tacCaseResponseDto.setCaseStatus(CaseStatus.valueOf(createTicketResponseDto.getStatusForTickets().name()));
        tacCaseResponseDto.setCasePriority(CasePriorityEnum.valueOf(createTicketResponseDto.getPriorityForTickets().name()));
        tacCaseResponseDto.setCaseCreatedDate(createTicketResponseDto.getCreatedAt());

        return tacCaseResponseDto;

    }

    /*
     * Update a TAC Case in Freshdesk
     */
    @Override
    public TacCaseResponseDto update(Long caseId, TacCaseUpdateDto tacCaseUpdateDto) {

        /*
         * A little housekeeping
         */
        String subject = tacCaseUpdateDto.getSubject();
        if (escapeHtmlSubject && !escapeHtmlStrings && subject != null) {
            tacCaseUpdateDto.setSubject(HtmlUtils.htmlEscape(subject));
        }

        String problemDescription = tacCaseUpdateDto.getProblemDescription();
        if (escapeHtmlDescription && !escapeHtmlStrings && problemDescription != null) {
            tacCaseUpdateDto.setProblemDescription(HtmlUtils.htmlEscape(problemDescription));
        }


        //First make sure this is a valid TAC Case
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);
        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing TAC Case Number.", "INVALID_CASE");
        }

        FreshdeskTicketUpdateDto freshdeskTicketUpdateDto = buildFreshdeskTicketUpdateDto(tacCaseUpdateDto);

        //First, update the ticket if needed
        if (freshdeskTicketUpdateDto != null) {
            updateTicket(caseId, freshdeskTicketUpdateDto);
        }

        //Now, fetch it with built-in stats for the TAC Case Response details
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);

        assert freshdeskTicketResponseDto != null; //fixme: what happens here if null?

/*
        Now update the Freshdesk TAC Case Custom Object
        So, we need to find that TAC Case associated with this Freshdesk Ticket
        We need the Schema ID to build the URL
        The response body we are looking for here is the same as the response from
        a create but since this is a Freshdesk Query, by design, there could be more than one "record",
        or row, and the response comes wrapped in an array called "records". In our case,
        there will ever only be one record returned.
*/
        FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> freshdeskCaseResponseRecords =
                findFreshdeskTacCaseRecords(caseId);
/*
        If there is a record, there will only be one as this is a 1:1 relationship by design
        The record ID here is not an integer like with a ticket
*/
        Optional<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> freshdeskCaseResponse =
                freshdeskCaseResponseRecords.getRecords().stream().findFirst();
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto =
                freshdeskCaseResponse.map(FreshdeskCaseResponse::getData).orElse(null);

        //get the record's identifier, this is what we need to update the record
        String tacCaseDisplayId = freshdeskCaseResponse.get().getDisplayId();

/*
         The Custom Objects API documentation is wrong. One must update all the
         custom object fields with the current values as a partial update isn't
         working on custom objects.
         The following is from the documentation.
         { "display_id": "BKG-1", "data": { "product_name": "Fiber Concentrator", "product_firmware_version": "v1.23.4" } }
*/
        // Map all the field values from the TAC Case query response to the update dto
        FreshdeskTacCaseUpdateDto freshdeskTacCaseUpdateDto = genericModelMapper.map(freshdeskTacCaseResponseDto, FreshdeskTacCaseUpdateDto.class);
        //fixme: we probably need to move this out of the TAC Case Object in Freshdesk
        CasePriorityEnum priority = CasePriorityEnum.valueOf(freshdeskTicketResponseDto.getPriorityForTickets().name());
        //fixme: we probably need to move this out of the TAC Case Object in Freshdesk
        freshdeskTacCaseUpdateDto.setCasePriority(priority);
        CaseStatus status = CaseStatus.valueOf(freshdeskTicketResponseDto.getStatusForTickets().name());
        freshdeskTacCaseUpdateDto.setCaseStatus(status);
        freshdeskTacCaseUpdateDto.setSubject(freshdeskTicketResponseDto.getSubject());

        // Now map all the "fields present" in the TacCaseUpdateDto.
        FieldPresenceModelMapper fieldPresenceModelMapper = new FieldPresenceModelMapper();
        fieldPresenceModelMapper.map(tacCaseUpdateDto, freshdeskTacCaseUpdateDto);

        // Now wrap it to have the display_id, version (also not documented), and the request in an element called data:)
        FreshdeskTacCaseUpdateRequest updateRequest = new FreshdeskTacCaseUpdateRequest(freshdeskTacCaseUpdateDto);
        updateRequest.setDisplayId(tacCaseDisplayId);
        updateRequest.setVersion(freshdeskCaseResponse.get().getVersion());

        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> response = createFreshdeskTacCase(tacCaseDisplayId, updateRequest);

        FreshdeskTacCaseResponseDto responseData = response.getData();
        TacCaseResponseDto tacCaseResponseDto = genericModelMapper.map(responseData, TacCaseResponseDto.class);
        tacCaseResponseDto.setId(freshdeskTicketResponseDto.getId());
        tacCaseResponseDto.setSubject(freshdeskTicketResponseDto.getSubject());
//        tacCaseResponseDto.setCaseOwner(requesterResponderService.getResponderName());
        tacCaseResponseDto.setProblemDescription(freshdeskTicketResponseDto.getDescriptionText());
        tacCaseResponseDto.setCaseStatus(status);
        tacCaseResponseDto.setCasePriority(priority);
        tacCaseResponseDto.setCaseNumber(tacCaseDisplayId);
        tacCaseResponseDto.setFirstResponseDate(freshdeskTicketResponseDto.getStats().getFirstRespondedAt());
        tacCaseResponseDto.setCaseClosedDate(freshdeskTicketResponseDto.getStats().getClosedAt());
        tacCaseResponseDto.setCaseCreatedDate(freshdeskTicketResponseDto.getCreatedAt());

        return tacCaseResponseDto;
    }


    //fixme: Find All needs Contract for Paging Results
    @Override
    public List<TacCaseResponseDto> findAll() {

        return List.of();
    }

    @Override
    public Optional<TacCaseResponseDto> findById(Long id) {
        return findFreshdeskTacCaseByTicketId(id)
                .flatMap(tacCase -> Optional.ofNullable(findFreshdeskTicketById(id))
                        .map(ticket -> mapToTacCaseResponseDto(tacCase, ticket)));
    }

    //fixme
    @Override
    public boolean exists(Long id) {
        return false;
    }

    //fixme
    @Override
    public void delete(Long id) {

    }

    /*
     * Add attachments to an existing ticket
     * fixme: this should be addAttachment perhaps?
     *  fixme: I have already worked on this?????????????
     */
    public FreshdeskTicketResponseDto addAttachments(Long ticketId, List<TicketAttachmentUploadDto> attachments) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        attachments.forEach(attachment -> {
            bodyBuilder.part("attachments[]", attachment.getFile().getResource())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "form-data; name=\"attachments[]\"; filename=\"" + attachment.getFile().getOriginalFilename() + "\"");
        });

        MultiValueMap<String, HttpEntity<?>> multipartBody = bodyBuilder.build();

        try {
            return snakeCaseRestClient.put()
                    .uri("/tickets/{ticketId}", ticketId)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .body(FreshdeskTicketResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error adding attachments to ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public TacCaseAttachmentResponseDto addAttachment(Long caseId, TacCaseAttachmentUploadDto uploadDto) throws IOException {

        // Validate the uploaded file
        MultipartFile file = uploadDto.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must be provided and not empty");
        }

        validateFileType(file);

        //First make sure this is a valid TAC Case
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);
        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing TAC Case Number.", "INVALID_CASE");
        }

        // Create the multipart request body
        MultiValueMap<String, HttpEntity<?>> multipartMap = createMultipartMap(file);

        try {
            FreshdeskTicketResponseDto dto = snakeCaseRestClient.put()
                    .uri("/tickets/{ticketId}", caseId)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartMap)
                    .retrieve()
                    .body(FreshdeskTicketResponseDto.class);

            if (dto == null || dto.getAttachments() == null || dto.getAttachments().isEmpty()) {
                throw new RuntimeException("Freshdesk response does not contain any attachments.");
            }

            return mapToAttachmentResponse(dto, file.getOriginalFilename());

        } catch (Exception e) {
            throw new RestClientException("Failed to upload attachment to Freshdesk: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacCaseAttachmentResponseDto> getAllAttachments(Long caseId) {

        //First make sure this is a valid TAC Case
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);
        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing TAC Case Number.", "INVALID_CASE");
        }

        // Retrieve the ticket and its attachments
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);
        List<FreshdeskAttachment> freshdeskAttachments = freshdeskTicketResponseDto.getAttachments();

        // Handle potential null attachments
        if (freshdeskAttachments == null || freshdeskAttachments.isEmpty()) {
            return List.of(); // Return an empty list if no attachments are found
        }

        // Map attachments to TacCaseAttachmentResponseDto and collect to a list
        return freshdeskAttachments.stream()
                .map(attachment -> TacCaseAttachmentResponseDto.builder()
                        .id(attachment.getId())
                        .name(attachment.getName())
                        .mimeType(attachment.getContentType())
                        .description(attachment.getAttachmentUrl())
                        .build())
                .toList();
    }

    @Override
    public TacCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId) {

        //First make sure this is a valid TAC Case
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);
        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing TAC Case Number.", "INVALID_CASE");
        }

        // Retrieve the ticket and its attachments
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);
        if (freshdeskTicketResponseDto == null) {
            throw new ResourceNotFoundException("No required ticket found for TAC Case: " + caseId, "INVALID_CASE");
        }

        List<FreshdeskAttachment> freshdeskAttachments = freshdeskTicketResponseDto.getAttachments();
        if (freshdeskAttachments == null || freshdeskAttachments.isEmpty()) {
            throw new IllegalArgumentException("No attachments found for TAC Case: " + caseId);
        }

        // Filter, map, and return the first matching attachment
        return freshdeskAttachments.stream()
                .filter(attachment -> Objects.equals(attachment.getId(), attachmentId))
                .findFirst()
                .map(attachment -> TacCaseAttachmentResponseDto.builder()
                        .id(attachment.getId())
                        .name(attachment.getName())
                        .mimeType(attachment.getContentType())
                        .description(attachment.getAttachmentUrl())
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("No attachment found for TAC Case: " + caseId + " with attachment ID: " + attachmentId));
    }

    //fixme:
    @Override
    public TacCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId) {
        return null;
    }

    //fixme:
    @Override
    public void deleteAttachment(Long caseId, Long attachmentId) {

    }

    //fixme:
    @Override
    public void deleteAllAttachments(Long caseId) {

    }

    //fixme: Should this throw an exception where the other's don't?
    @Override
    public TacCaseNoteResponseDto addNote(Long caseId, TacCaseNoteUploadDto uploadDto) throws IOException {

        //First make sure this is a valid TAC Ticket
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);

        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        String text = uploadDto.getText();
        if (escapeHtmlNotes && !escapeHtmlStrings && text != null) { //probably don't need the null check here
            uploadDto.setText(HtmlUtils.htmlEscape(text));
        }

        FreshdeskTicketCreateNoteDto dto = FreshdeskTicketCreateNoteDto.builder()
                .body(uploadDto.getText())
                .privateField(false)
                .incoming(true)
                .build();

        FreshdeskTicketNoteResponseDto freshdeskTicketNoteDto = createFreshdeskTicketNote(dto, caseId);

        return TacCaseNoteResponseDto.builder()
                .id(freshdeskTicketNoteDto.getId())
                .author(uploadDto.getAuthor())
                .tacCaseId(caseId)
                .date(freshdeskTicketNoteDto.getCreatedAt())
                .build();
    }

    @Override
    public List<TacCaseNoteResponseDto> getAllNotes(Long caseId) {

        //First make sure this is a valid TAC Ticket
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);

        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        //first get all the notes of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        return freshdeskTicketConversations.stream()
                .filter(freshdesk -> freshdesk.getSource() == FreshdeskConversationSource.Note)
                .filter(freshdesk -> !freshdesk.isPrivate())
                .map(freshdesk -> TacCaseNoteResponseDto.builder()
                        .id(freshdesk.getId())
                        .tacCaseId(caseId)
                        .author("FD User ID:" + freshdesk.getUserId().toString())
                        .date(freshdesk.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public TacCaseNoteDownloadDto getNote(Long caseId, Long noteId) {

        //First make sure this is a valid TAC Ticket
        Optional<TacCaseResponseDto> freshdeskTacCaseByTicketId = findFreshdeskTacCaseByTicketId(caseId);

        if (freshdeskTacCaseByTicketId.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        // Retrieve all the conversations of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        // Find and map the note to TacCaseNoteDownloadDto
        return freshdeskTicketConversations.stream()
                .filter(freshdesk -> Objects.equals(freshdesk.getId(), noteId))
                .map(freshdesk -> TacCaseNoteDownloadDto.builder()
                        .id(freshdesk.getId())
                        .tacCaseId(caseId)
                        .author("FD User ID:" + freshdesk.getUserId())
                        .date(freshdesk.getCreatedAt())
                        .text(freshdesk.getBodyText())
                        .build())
                .findFirst() // Get the first matching note
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No note found with noteId: " + noteId + " for Case ID: " + caseId, "INVALID_CASE"));
    }

    @Override
    public void deleteNote(Long caseId, Long noteId) {

    }

    @Override
    public void deleteAllNotes(Long caseId) {

    }

    @Override
    public List<RmaCaseResponseDto> listRmaCases(Long id) {

        return List.of();
    }



    /*
    Helper Methods
     */

    /*
    TAC Cases
     */
    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> createFreshdeskTacCase(String displayId, FreshdeskTacCaseUpdateRequest updateRequest) {
        String tacCaseSchemaId = schemaService.getTacCaseSchemaId();
        return snakeCaseRestClient.put()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", tacCaseSchemaId, displayId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseRecords(Long id) {
        String tacCaseSchemaId = schemaService.getTacCaseSchemaId();
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/" + tacCaseSchemaId + "/records?ticket={ticketId}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskTicketResponseDto updateTicket(Long id, FreshdeskTicketUpdateDto freshdeskTicketUpdateDto) {
        return fieldPresenseRestClient.put()
                .uri("/tickets/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(freshdeskTicketUpdateDto)
                .retrieve()
                .body(FreshdeskTicketResponseDto.class);
    }

    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> createFreshdeskTacCase(
            FreshdeskDataCreateRequest<FreshdeskTacCaseCreateDto> freshdeskTacCaseCreateRequest) {

        String tacCaseSchemaId = schemaService.getTacCaseSchemaId();
        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> responseTacCase = snakeCaseRestClient.post()
                .uri("/custom_objects/schemas/{schemaId}/records", tacCaseSchemaId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(freshdeskTacCaseCreateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        assert responseTacCase != null;
        return responseTacCase;
    }



    /*
    RMA Cases
     */
    //fixme: why isn't this used?
    private static FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> createFreshdeskRmaCase(String rmaCaseSchemaId,
                           FreshdeskDataCreateRequest<FreshdeskRmaCaseCreateDto> freshdeskRmaCaseCreateRequest,
                           RestClient restClient) {

        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> responseRmaCase = restClient.post()
                .uri("/custom_objects/schemas/{schemaId}/records", rmaCaseSchemaId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(freshdeskRmaCaseCreateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        assert responseRmaCase != null;
        return responseRmaCase;
    }


    private FreshdeskTicketCreateDto buildCreateTicketDto(TacCaseCreateDto tacCaseDto, String responderId, String requesterId, String companyId) {

        return FreshdeskTicketCreateDto.builder()
                .email(tacCaseDto.getContactEmail())
                .subject(tacCaseDto.getSubject())
                .responderId(Long.valueOf(responderId))
                .requesterId(Long.valueOf(requesterId))
                .companyId(Long.valueOf(companyId))
                .type("Problem")
                .source(Source.Email)
                .status(StatusForTickets.Open)
                .priority(Optional.ofNullable(tacCaseDto.getCasePriority()) //fixme?
                        .map(CasePriorityEnum::getValue)
                        .map(PriorityForTickets::valueOf)
                        .orElse(null))
                .description(tacCaseDto.getProblemDescription())
                .tags(List.of("TAC"))
                .build();
    }

    private FreshdeskTicketResponseDto createFreshdeskTicket(FreshdeskTicketCreateDto dto) {
        return snakeCaseRestClient.post()
                .uri("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(FreshdeskTicketResponseDto.class);
    }

    private FreshdeskTicketNoteResponseDto createFreshdeskTicketNote(FreshdeskTicketCreateNoteDto dto, Long id) {
        return snakeCaseRestClient.post()
                .uri("/tickets/{id}/notes", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(FreshdeskTicketNoteResponseDto.class);
    }

    private List<FreshdeskTicketConversationDto> findFreshdeskTicketConversations(Long id) {

        return snakeCaseRestClient.get()
                .uri("/tickets/{id}/conversations", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

    }

    private Optional<TacCaseResponseDto> findFreshdeskTacCaseByTicketId(Long id) {
        return findFreshdeskTacCaseRecords(id)
                .getRecords()
                .stream()
                .findFirst() // Expecting only one record
                .map(record -> {
                    TacCaseResponseDto tacCaseResponseDto = genericModelMapper.map(record.getData(), TacCaseResponseDto.class);
                    tacCaseResponseDto.setCaseNumber(record.getDisplayId());
                    return tacCaseResponseDto;
                });
    }

    /*
      Always including stats in response to be able to send the "firstRespondedDated" field TAC Case Response
     */
    private FreshdeskTicketResponseDto findFreshdeskTicketById(Long id) {
        return snakeCaseRestClient.get()
                .uri("/tickets/{id}?include=stats", id)
                .retrieve()
                .body(FreshdeskTicketResponseDto.class);
    }

    //fixme: look into this one
    private static FreshdeskTicketUpdateDto buildFreshdeskTicketUpdateDto(TacCaseUpdateDto tacCaseUpdateDto) {

        boolean requiresUpdate = false;

        FreshdeskTicketUpdateDto freshdeskTicketUpdateDto = new FreshdeskTicketUpdateDto();
        if (tacCaseUpdateDto.isFieldPresent("subject")) {
            freshdeskTicketUpdateDto.setSubject(tacCaseUpdateDto.getSubject());
            requiresUpdate = true;
        }

        if (tacCaseUpdateDto.isFieldPresent("contactEmail")) {
            freshdeskTicketUpdateDto.setEmail(tacCaseUpdateDto.getContactEmail());
            requiresUpdate = true;
        }

        if (tacCaseUpdateDto.isFieldPresent("problemDescription")) {
            freshdeskTicketUpdateDto.setDescription(tacCaseUpdateDto.getProblemDescription());
            requiresUpdate = true;
        }

        if (tacCaseUpdateDto.isFieldPresent("caseStatus")) {
            freshdeskTicketUpdateDto.setStatus(StatusForTickets.valueOf(tacCaseUpdateDto.getCaseStatus().getValue()));
            requiresUpdate = true;
        }

        if (tacCaseUpdateDto.isFieldPresent("casePriority")) {
            freshdeskTicketUpdateDto.setPriority(PriorityForTickets.valueOf(tacCaseUpdateDto.getCasePriority().getValue()));
            requiresUpdate = true;
        }
        if (requiresUpdate) {
            return freshdeskTicketUpdateDto;
        } else {
            return null;
        }
    }

    /**
     * Validates the MIME type of the uploaded file.
     * @param file the uploaded MultipartFile
     */
    private void validateFileType(MultipartFile file) {
        List<String> allowedMimeTypes = Arrays.asList(
                "application/pdf",
                "application/msword", //.doc
                "application/vnd.ms-excel", //.xls
                "application/vnd.ms-powerpoint", //.ppt
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", //.xlsx
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", //.docx
                "application/vnd.openxmlformats-officedocument.presentationml.presentation", //.pptx
                "text/plain",
                "image/jpeg",
                "image/png",
                "image/gif",
                "application/zip",
                "application/x-7z-compressed",
                "application/x-rar-compressed",
                "application/json",
                "application/xml",
                "text/csv"); // Extend as needed

        // Validate file type
        if (!allowedMimeTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType()
                    + ". Allowed types: " + String.join(", ", allowedMimeTypes));
        }

    }

    private MultiValueMap<String, HttpEntity<?>> createMultipartMap(MultipartFile file) throws IOException {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder
                .part("attachments[]", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                });

        return bodyBuilder.build();
    }

    private TacCaseAttachmentResponseDto mapToAttachmentResponse(FreshdeskTicketResponseDto ticketResponseDto, String fileName) {
        // Find the attachment matching the fileName
        FreshdeskAttachment freshdeskAttachment = ticketResponseDto.getAttachments().stream()
                .filter(attachment -> attachment.getName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attachment with name '" + fileName + "' not found"));

        // Map the FreshdeskAttachment to TacCaseAttachmentResponseDto
        return TacCaseAttachmentResponseDto.builder()
                .id(freshdeskAttachment.getId())
                .mimeType(freshdeskAttachment.getContentType())
                .name(freshdeskAttachment.getName())
                .description(freshdeskAttachment.getAttachmentUrl())
                .size(freshdeskAttachment.getSize().floatValue())
                .build();
    }

    private TacCaseResponseDto mapToTacCaseResponseDto(FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse) {

        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = freshdeskTacCaseResponse.getData();
        TacCaseResponseDto tacCaseResponseDto = genericModelMapper.map(freshdeskTacCaseResponseDto, TacCaseResponseDto.class);
        tacCaseResponseDto.setCaseNumber(freshdeskTacCaseResponse.getDisplayId());
        tacCaseResponseDto.setId(freshdeskTacCaseResponseDto.getTicket());

        //fixme: someday we need to fix this because this will be costly to the responsiveness of the API
        //need to get the ticket for each of the responses to set
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(freshdeskTacCaseResponseDto.getTicket());
        tacCaseResponseDto.setCaseStatus(CaseStatus.valueOf(freshdeskTicketResponseDto.getStatusForTickets().name()));
        tacCaseResponseDto.setCasePriority(CasePriorityEnum.valueOf(freshdeskTicketResponseDto.getPriorityForTickets().name()));
        tacCaseResponseDto.setSubject(freshdeskTicketResponseDto.getSubject());
//        tacCaseResponseDto.setCaseOwner(requesterResponderService.getResponderName());
        tacCaseResponseDto.setProblemDescription(freshdeskTicketResponseDto.getDescriptionText());
        tacCaseResponseDto.setCaseNumber(freshdeskTacCaseResponse.getDisplayId());
        tacCaseResponseDto.setFirstResponseDate(freshdeskTicketResponseDto.getStats().getFirstRespondedAt());
        tacCaseResponseDto.setCaseClosedDate(freshdeskTicketResponseDto.getStats().getClosedAt());
        tacCaseResponseDto.setCaseCreatedDate(freshdeskTicketResponseDto.getCreatedAt());

        return tacCaseResponseDto;
    }

    private TacCaseResponseDto mapToTacCaseResponseDto(TacCaseResponseDto tacCase, FreshdeskTicketResponseDto ticket) {
        tacCase.setId(ticket.getId());
        tacCase.setSubject(ticket.getSubject());
//        tacCase.setCaseOwner(requesterResponderService.getResponderName());
        tacCase.setCaseStatus(CaseStatus.valueOf(ticket.getStatusForTickets().name()));
        tacCase.setCasePriority(CasePriorityEnum.valueOf(ticket.getPriorityForTickets().name()));
        tacCase.setProblemDescription(ticket.getDescriptionText());
        tacCase.setFirstResponseDate(ticket.getStats().getFirstRespondedAt());
        tacCase.setCaseClosedDate(ticket.getStats().getClosedAt());
        tacCase.setCaseCreatedDate(ticket.getCreatedAt());
        return tacCase;
    }



}
