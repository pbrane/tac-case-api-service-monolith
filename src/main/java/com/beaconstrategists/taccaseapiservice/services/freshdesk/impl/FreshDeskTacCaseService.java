package com.beaconstrategists.taccaseapiservice.services.freshdesk.impl;

import com.beaconstrategists.taccaseapiservice.config.freshdesk.RestClientConfig;
import com.beaconstrategists.taccaseapiservice.dtos.freshdesk.*;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.FieldPresenceModelMapper;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.GenericModelMapper;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.FreshdeskConversationSource;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.PriorityForTickets;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.CompanyService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.SchemaService;
import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.services.TacCaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("FreshdeskTacCaseService")
public class FreshDeskTacCaseService implements TacCaseService {

    private final RestClientConfig restClientConfig;
    private final RestClient snakeCaseRestClient;

    /*
     * This RestClient is only needed for updating Tickets
     * Freshdesk Tickets can handle partial updates.
     * Freshdesk Custom Objects (TAC/RMA Cases) require full updates.
     * The Custom Objects are managed with a FieldPresence ModelMapper.
     * So, they don't need, and shouldn't use, this RestClient
     */
    private final RestClient fieldPresenseRestClient;

    private final CompanyService companyService;

    private final GenericModelMapper genericModelMapper;
    private final ObjectMapper snakeCaseObjectMapper;

    @Value("${FD_CUSTOMER_NAME:Beacon}")
    private String companyName;

    @Value("${FD_DEFAULT_RESPONDER_ID:3043029172572}")
    private String defaultResponderId;

    @Value("${FD_TAC_CASE_ID_PREFIX:_2-}")
    private String tacCaseIdPrefix;

    private final SchemaService schemaService;

    public FreshDeskTacCaseService(RestClientConfig restClientConfig, @Qualifier("snakeCaseRestClient") RestClient snakeCaseRestClient,
                                   SchemaService schemaService,
                                   CompanyService companyService,
                                   GenericModelMapper genericModelMapper,
                                   @Qualifier("fieldPresenceSnakeCaseSerializingRestClient") RestClient fieldPresenseRestClient,
                                   @Qualifier("snakeCaseObjectMapper") ObjectMapper snakeCaseObjectMapper) {
        this.restClientConfig = restClientConfig;
        this.snakeCaseRestClient = snakeCaseRestClient;
        this.companyService = companyService;
        this.genericModelMapper = genericModelMapper;
        this.schemaService = schemaService;
        this.fieldPresenseRestClient = fieldPresenseRestClient;
        this.snakeCaseObjectMapper = snakeCaseObjectMapper;
    }

    @Override
    public List<TacCaseResponseDto> listTacCases(OffsetDateTime caseCreateDateFrom,
                                                 OffsetDateTime caseCreateDateTo,
                                                 OffsetDateTime caseCreateDateSince,
                                                 List<CaseStatus> caseStatus, String logic) {

        String schemaId = schemaService.getSchemaIdByName("TAC Cases");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Build the query parameters dynamically
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        if (caseCreateDateFrom != null && caseCreateDateTo != null) {
            uriComponentsBuilder.queryParam("created_time[gt]", caseCreateDateFrom.format(formatter))
                    .queryParam("created_time[lt]", caseCreateDateTo.format(formatter));
        } else if (caseCreateDateSince != null) {
            uriComponentsBuilder.queryParam("created_time[gt]", caseCreateDateSince.format(formatter));
        }

        //fixme: to get this to work, we will have to query tickets then fetch the matching TAC Cases
        if (caseStatus != null && !caseStatus.isEmpty()) {
            for (CaseStatus status : caseStatus) {
                uriComponentsBuilder.queryParam("status", status.getValue());
            }
        }

        RestClient restClient = snakeCaseRestClient.mutate()
                .baseUrl(restClientConfig.getFreshdeskBaseUri().endsWith("/")
                        ? restClientConfig.getFreshdeskBaseUri()
                        : restClientConfig.getFreshdeskBaseUri() + "/") // Ensure trailing "/"
                .build();

        // Pass the relative path without leading '/' and append the query parameters
        FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> responseRecords = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("custom_objects/schemas/" + schemaId + "/records") // No leading '/'
                        .query(Objects.requireNonNull(uriComponentsBuilder.build().getQuery()))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        assert responseRecords != null;

        List<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> freshdeskCaseResponses = responseRecords.getRecords().stream().toList();
        List<TacCaseResponseDto> tacCaseResponseDtos = freshdeskCaseResponses.stream()
                .map(this::mapToTacCaseResponseDto)
                .toList();

        // Return the list of records
        return tacCaseResponseDtos;
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

        return tacCaseResponseDto;

    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String formatDate(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }


    /*
     * Create a TAC Case in Freshdesk
     */
    @Override
    public TacCaseResponseDto create(TacCaseCreateDto tacCaseCreateDto) {

        FreshdeskTicketCreateDto freshdeskTicketCreateDto = buildCreateTicket(tacCaseCreateDto, defaultResponderId);
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = createFreshdeskTicket(freshdeskTicketCreateDto);

        assert freshdeskTicketResponseDto != null;  //fixme: what happens here if null

        FreshdeskTacCaseCreateDto freshdeskTacCaseCreateDto = genericModelMapper.map(tacCaseCreateDto, FreshdeskTacCaseCreateDto.class);
        freshdeskTacCaseCreateDto.setKey("ID: " + freshdeskTicketResponseDto.getId() + "; " + tacCaseCreateDto.getSubject());
        freshdeskTacCaseCreateDto.setTicket(freshdeskTicketResponseDto.getId());
        //fixme: FreshdeskDataCreateRequest and FreshdeskTacCaseRequest are identical
        FreshdeskDataCreateRequest<FreshdeskTacCaseCreateDto> freshdeskTacCaseCreateRequest = new FreshdeskDataCreateRequest<>(freshdeskTacCaseCreateDto);

        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse = updateFreshdeskTacCase(freshdeskTacCaseCreateRequest);
        FreshdeskTacCaseResponseDto data = freshdeskTacCaseResponse.getData();

        TacCaseResponseDto responseDto = genericModelMapper.map(data, TacCaseResponseDto.class);
        responseDto.setCaseNumber(freshdeskTacCaseResponse.getDisplayId());
        responseDto.setSubject(freshdeskTicketResponseDto.getSubject());
        responseDto.setId(freshdeskTicketResponseDto.getId());

        //fixme: to do this right, we have to do an update with this value
        //responseDto.setCaseNumber(freshdeskTacCaseResponse.getDisplayId());
        return responseDto;

    }

    /*
     * Update a TAC Case in Freshdesk
     */
    @Override
    public TacCaseResponseDto update(Long id, TacCaseUpdateDto tacCaseUpdateDto) {

        FreshdeskTicketResponseDto freshdeskTicketResponseDto;

        FreshdeskTicketUpdateDto freshdeskTicketUpdateDto = buildFreshdeskTicketUpdateDto(tacCaseUpdateDto);
        if (freshdeskTicketUpdateDto != null) {
            freshdeskTicketResponseDto = updateTicket(id, freshdeskTicketUpdateDto);
        } else {
            freshdeskTicketResponseDto = snakeCaseRestClient.get()
                    .uri("/tickets/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FreshdeskTicketResponseDto.class);
        }

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
                findFreshdeskTacCaseRecords(id);
/*
        If there is a record, there will only be one as this is a 1:1 relationship by design
        The record ID here is not an integer like with a ticket
*/
        Optional<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> freshdeskCaseResponse = freshdeskCaseResponseRecords.getRecords().stream().findFirst();
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = freshdeskCaseResponse.map(FreshdeskCaseResponse::getData).orElse(null);

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
        //fixme: we probably need to move this out of the TAC Case Object
        CasePriorityEnum priority = CasePriorityEnum.valueOf(freshdeskTicketResponseDto.getPriorityForTickets().name());
        //fixme: we probably need to move this out of the TAC Case Object
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

        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> response = updateFreshdeskTacCase(tacCaseDisplayId, updateRequest);

        FreshdeskTacCaseResponseDto responseData = response.getData();
        TacCaseResponseDto tacCaseResponseDto = genericModelMapper.map(responseData, TacCaseResponseDto.class);
        tacCaseResponseDto.setId(freshdeskTicketResponseDto.getId());
        tacCaseResponseDto.setSubject(freshdeskTicketResponseDto.getSubject());
        tacCaseResponseDto.setCaseStatus(status);
        tacCaseResponseDto.setCasePriority(priority);
        tacCaseResponseDto.setCaseNumber(tacCaseDisplayId);
        return tacCaseResponseDto;
    }


    //fixme: Find All needs Contract for Paging Results
    @Override
    public List<TacCaseResponseDto> findAll() {

        return List.of();
    }

    @Override
    public Optional<TacCaseResponseDto> findById(Long id) {

/*
         These finds will never be null.
         If any of the RestClient queries fail to find this ID,
         The RestClient will result in an HttpClientErrorException.NotFound
         and will propagate back to the Controller.
*/

        TacCaseResponseDto tacCaseResponseDto = findFreshdeskTacCaseByTicketId(id);
        tacCaseResponseDto.setId(id);

        FreshdeskTicketResponseDto ticketResponseDto = findFreshdeskTicketById(id);

        tacCaseResponseDto.setSubject(ticketResponseDto.getSubject());
        tacCaseResponseDto.setCaseStatus(CaseStatus.valueOf(ticketResponseDto.getStatusForTickets().name()));
        tacCaseResponseDto.setCasePriority(CasePriorityEnum.valueOf(ticketResponseDto.getPriorityForTickets().name()));

        return Optional.of(tacCaseResponseDto);
    }

    @Override
    public boolean exists(Long id) {
        return false;
    }

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
    public TacCaseAttachmentResponseDto addAttachment(Long ticketId, TacCaseAttachmentUploadDto uploadDto) throws IOException {
        // Validate the uploaded file
        MultipartFile file = uploadDto.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must be provided and not empty");
        }

        validateFileType(file);

        // Create the multipart request body
        MultiValueMap<String, HttpEntity<?>> multipartMap = createMultipartMap(file);

        try {
            FreshdeskTicketResponseDto dto = snakeCaseRestClient.put()
                    .uri("/tickets/{ticketId}", ticketId)
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
        // Retrieve the ticket and its attachments
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);
        if (freshdeskTicketResponseDto == null) {
            throw new IllegalArgumentException("No ticket found for TAC Case: " + caseId);
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

    @Override
    public TacCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId) {
        return null;
    }

    @Override
    public void deleteAttachment(Long caseId, Long attachmentId) {

    }

    @Override
    public void deleteAllAttachments(Long caseId) {

    }

    //fixme: Should this throw an exception where the other's don't?
    @Override
    public TacCaseNoteResponseDto addNote(Long ticketId, TacCaseNoteUploadDto uploadDto) throws IOException {

        FreshdeskTicketCreateNoteDto dto = FreshdeskTicketCreateNoteDto.builder()
                .body(uploadDto.getText())
                .privateField(false)
                .incoming(true)
                .build();

        FreshdeskTicketNoteResponseDto freshdeskTicketNoteDto = createFreshdeskTicketNote(dto, ticketId);

        return TacCaseNoteResponseDto.builder()
                .id(freshdeskTicketNoteDto.getId())
                .author(uploadDto.getAuthor())
                .tacCaseId(ticketId)
                .date(freshdeskTicketNoteDto.getCreatedAt())
                .build();
    }

    @Override
    public List<TacCaseNoteResponseDto> getAllNotes(Long caseId) {

        //first get all the notes of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        return freshdeskTicketConversations.stream()
                .filter(freshdesk -> freshdesk.getSource() == FreshdeskConversationSource.Note)
                .map(freshdesk -> TacCaseNoteResponseDto.builder()
                        .id(freshdesk.getId())
                        .tacCaseId(caseId)
                        .author("FD User ID:"+freshdesk.getUserId().toString())
                        .date(freshdesk.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public TacCaseNoteDownloadDto getNote(Long caseId, Long noteId) {
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
                .orElseThrow(() -> new IllegalArgumentException(
                        "No note found with noteId: " + noteId + " for Case ID: " + caseId));
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
    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> updateFreshdeskTacCase(String displayId, FreshdeskTacCaseUpdateRequest updateRequest) {
        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
        return snakeCaseRestClient.put()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", tacCaseSchemaId, displayId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseRecords(Long id) {
        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
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

    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> updateFreshdeskTacCase(
            FreshdeskDataCreateRequest<FreshdeskTacCaseCreateDto> freshdeskTacCaseCreateRequest) {

        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
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


    private FreshdeskTicketCreateDto buildCreateTicket(TacCaseCreateDto tacCaseDto, String responderId) {

        return FreshdeskTicketCreateDto.builder()
                .email(tacCaseDto.getContactEmail())
                .subject(tacCaseDto.getSubject())
                .responderId(Long.valueOf(responderId))
                .type("Problem")
                .source(Source.Email)
                .status(StatusForTickets.Open)
                .priority(Optional.ofNullable(tacCaseDto.getCasePriority()) //fixme?
                        .map(CasePriorityEnum::getValue)
                        .map(PriorityForTickets::valueOf)
                        .orElse(null))
                .description(tacCaseDto.getProblemDescription())
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

    private TacCaseResponseDto findFreshdeskTacCaseByTicketId(Long id) {
        FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> freshdeskTacCaseRecords = findFreshdeskTacCaseRecords(id);
        Optional<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> record = freshdeskTacCaseRecords.getRecords().stream().findFirst();
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = record.map(FreshdeskCaseResponse::getData).orElse(null);
        return genericModelMapper.map(freshdeskTacCaseResponseDto, TacCaseResponseDto.class);
    }

    private FreshdeskTicketResponseDto findFreshdeskTicketById(Long id) {
        return snakeCaseRestClient.get()
                .uri("/tickets/{id}", id)
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
                "application/msword",
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

}
