package com.beaconstrategists.taccaseapiservice.services.freshdesk.impl;

import com.beaconstrategists.taccaseapiservice.config.freshdesk.RestClientConfig;
import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.dtos.freshdesk.*;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.FieldPresenceModelMapper;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.GenericModelMapper;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.FreshdeskConversationSource;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.beaconstrategists.taccaseapiservice.services.RmaCaseService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.SchemaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("FreshdeskRmaCaseService")
public class FreshDeskRmaCaseService implements RmaCaseService {

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
    private final GenericModelMapper genericModelMapper;

    private final SchemaService schemaService;

    public FreshDeskRmaCaseService(RestClientConfig restClientConfig,
                                   @Qualifier("snakeCaseRestClient") RestClient snakeCaseRestClient,
                                   SchemaService schemaService,
                                   GenericModelMapper genericModelMapper,
                                   @Qualifier("fieldPresenceSnakeCaseSerializingRestClient") RestClient fieldPresenseRestClient) {
        this.restClientConfig = restClientConfig;
        this.snakeCaseRestClient = snakeCaseRestClient;
        this.genericModelMapper = genericModelMapper;
        this.schemaService = schemaService;
        this.fieldPresenseRestClient = fieldPresenseRestClient;
    }


    /*
     * Create an RMA Case in Freshdesk
     */

    @Override
    public RmaCaseResponseDto create(RmaCaseCreateDto rmaCaseCreateDto) {


        /*
         * First we need to create a child ticket to the TAC Case's Ticket
         * fixme: we are assuming that a tac case ticket is found
         */
        FreshdeskTicketResponseDto tacCaseTicketResponseDto = findFreshdeskTicketById(rmaCaseCreateDto.getTacCaseId());
        FreshdeskTicketCreateDto rmaTicketCreateDto = buildCreateChildTicketDto(tacCaseTicketResponseDto, rmaCaseCreateDto);
        FreshdeskTicketResponseDto createRmaCaseTicketResponseDto = createFreshdeskTicket(rmaTicketCreateDto); //fixme: inline this sometime

        /*
         * We've created the child Ticket for the RMA, now create the RMA Case
         * and associate it with the child Ticket and the TAC Case
         */

        /*
         * Get the TAC Case
         */
        FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponseRecords =
                findFreshdeskTacCaseRecordsByTicketId(rmaCaseCreateDto.getTacCaseId());
        assert freshdeskTacCaseResponseRecords != null;

        Optional<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> tacCaseRecord =
                freshdeskTacCaseResponseRecords.getRecords().stream().findFirst();
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto =
                tacCaseRecord.map(FreshdeskCaseResponse::getData).orElse(null);

        FreshdeskRmaCaseCreateDto freshdeskRmaCaseCreateDto =
                genericModelMapper.map(rmaCaseCreateDto, FreshdeskRmaCaseCreateDto.class);
        assert freshdeskTacCaseResponseDto != null;

        freshdeskRmaCaseCreateDto.setKey("RMA:"+createRmaCaseTicketResponseDto.getId()+", "+ freshdeskTacCaseResponseDto.getKey());
        freshdeskRmaCaseCreateDto.setTacCase(tacCaseRecord.get().getDisplayId());
        freshdeskRmaCaseCreateDto.setTicket(createRmaCaseTicketResponseDto.getId());

        FreshdeskDataCreateRequest<FreshdeskRmaCaseCreateDto> freshdeskRmaCreateRequest =
                new FreshdeskDataCreateRequest<>(freshdeskRmaCaseCreateDto);
        String rmaCaseSchemaId = schemaService.getSchemaIdByName("RMA Cases");

        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> createRmaCaseResponse =
                snakeCaseRestClient.post()
                        .uri("/custom_objects/schemas/{schemaId}/records", rmaCaseSchemaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(freshdeskRmaCreateRequest)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });
        assert createRmaCaseResponse != null;

        String rmaDisplayId = createRmaCaseResponse.getDisplayId();

        RmaCaseResponseDto rmaCaseResponseDto =
                genericModelMapper.map(createRmaCaseResponse.getData(), RmaCaseResponseDto.class);
        rmaCaseResponseDto.setId(createRmaCaseTicketResponseDto.getId());
        rmaCaseResponseDto.setTacCaseId(rmaCaseCreateDto.getTacCaseId());
        rmaCaseResponseDto.setCaseStatus(CaseStatus.valueOf(createRmaCaseTicketResponseDto.getStatusForTickets().name()));
        //fixme: to do this right, we have to do an update to set this field in the RMA Case Record
        rmaCaseResponseDto.setCaseNumber(rmaDisplayId);
        rmaCaseResponseDto.setCaseCreatedDate(createRmaCaseTicketResponseDto.getCreatedAt());

        return rmaCaseResponseDto;
    }

    @Override
    public RmaCaseResponseDto update(Long rmaTicketId, RmaCaseUpdateDto rmaCaseUpdateDto) {

        /*
         * If the update contains caseStatus, we should also update the ticket
         * Otherwise, just fetch the ticket.
         */
        if (rmaCaseUpdateDto.isFieldPresent("caseStatus")) {
            FreshdeskTicketUpdateDto dto = new FreshdeskTicketUpdateDto();
            dto.setStatus(StatusForTickets.valueOf(rmaCaseUpdateDto.getCaseStatus().getValue()));
            FreshdeskTicketResponseDto rmaCaseUpdateTicketResponseDto = updateTicket(rmaTicketId, dto);
        }

        /*
         * Always do the find so we can use the stats metadata in the final Response DTO
         */
        FreshdeskTicketResponseDto rmaCaseTicketResponseDto = findFreshdeskTicketById(rmaTicketId);

        /*
         * Now, we need to find the RMA Case Record associated with this ticket ID
         * fixme: find a better way to handle these Optionals
         */
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecords =
                findFreshdeskRmaCaseRecordsByTicketId(rmaTicketId);
        Optional<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> freshdeskRmaCaseResponse =
                rmaCaseRecords.getRecords().stream().findFirst();
        FreshdeskRmaCaseResponseDto findRmaCaseResponseDto =
                freshdeskRmaCaseResponse.map(FreshdeskCaseResponse::getData).orElse(null);
        assert findRmaCaseResponseDto != null;

        /*
          Get the TacCase to get the Ticket ID to save in the RMA Update Response as tacCaseId
         */
        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse =
                findFreshdeskTacCaseByDisplayId(findRmaCaseResponseDto.getTacCase());

        /*
          We have to map in the existing values before mapping in the updates because
          Freshdesk Custom Objects don't support partial updates
        */
        FreshdeskRmaCaseUpdateDto freshdeskRmaCaseUpdateDto = genericModelMapper.
                map(findRmaCaseResponseDto, FreshdeskRmaCaseUpdateDto.class);

        /*
          Okay, now map the changes sent with the update request to the existing data
        */
        FieldPresenceModelMapper fieldPresenceModelMapper = new FieldPresenceModelMapper();
        fieldPresenceModelMapper.map(rmaCaseUpdateDto, freshdeskRmaCaseUpdateDto);

        /*
          Ready to send the RMA Case update
        */
        FreshdeskRmaCaseUpdateRequest freshdeskRmaCaseUpdateRequest = new FreshdeskRmaCaseUpdateRequest(freshdeskRmaCaseUpdateDto);
        freshdeskRmaCaseUpdateRequest.setDisplayId(freshdeskRmaCaseResponse.get().getDisplayId());
        freshdeskRmaCaseUpdateRequest.setVersion(freshdeskRmaCaseResponse.get().getVersion());
        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> rmaCaseUpdateResponse =
                updateRmaCase(freshdeskRmaCaseResponse.get().getDisplayId(), freshdeskRmaCaseUpdateRequest);
        assert rmaCaseUpdateResponse != null;
        FreshdeskRmaCaseResponseDto updateRmaCaseResponseDto = rmaCaseUpdateResponse.getData();

        /*
          Note: //fixme: be more clear in this comment
          This genericModelMapper returns an instance of FreshdeskRmaCaseResponseDto
          Really weird but fortunately, it works, the extra few fields added to the child class
          are not serialized in the response from the controller
        */
        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(updateRmaCaseResponseDto, RmaCaseResponseDto.class);

        /*
          Finish mapping things for the response from a combination of the RMA Ticket and the RMA Response meta data
        */
        rmaCaseResponseDto.setId(rmaCaseTicketResponseDto.getId());
        rmaCaseResponseDto.setTacCaseId(freshdeskTacCaseResponse.getData().getTicket());
        rmaCaseResponseDto.setCaseStatus(CaseStatus.valueOf(rmaCaseTicketResponseDto.getStatusForTickets().name()));
        //fixme: to do this right, we have to do an update to set this field in the RMA Case Record
        rmaCaseResponseDto.setCaseNumber(freshdeskRmaCaseResponse.get().getDisplayId());
        rmaCaseResponseDto.setCaseClosedDate(rmaCaseTicketResponseDto.getStats().getClosedAt());
        rmaCaseResponseDto.setCaseCreatedDate(rmaCaseTicketResponseDto.getCreatedAt());

        return rmaCaseResponseDto;
    }

    @Override
    public Optional<RmaCaseResponseDto> findById(Long rmaTicketId) {

        /*
         * Find the RMA Ticket
         */
        FreshdeskTicketResponseDto rmaCaseTicketResponseDto = findFreshdeskTicketById(rmaTicketId);

        /*
         * Find the RMA Case
         */
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecords =
                findFreshdeskRmaCaseRecordsByTicketId(rmaTicketId);
        Optional<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> freshdeskRmaCaseResponse =
                rmaCaseRecords.getRecords().stream().findFirst();
        FreshdeskRmaCaseResponseDto findRmaCaseResponseDto =
                freshdeskRmaCaseResponse.map(FreshdeskCaseResponse::getData).orElse(null);
        assert findRmaCaseResponseDto != null;


        /*
         * Find the parent TAC Case
         */

        //now we have to pull the TAC Case to get the TAC Case ID which is the Freshdesk Ticket ID
        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse =
                findFreshdeskTacCaseByDisplayId(findRmaCaseResponseDto.getTacCase());
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = freshdeskTacCaseResponse.getData();


        /*
         * Start mapping values
         */
        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(findRmaCaseResponseDto, RmaCaseResponseDto.class);
        /*
          Finish mapping things for the response from a combination of the RMA Ticket and the RMA Response meta data
        */
        rmaCaseResponseDto.setId(rmaCaseTicketResponseDto.getId());
        rmaCaseResponseDto.setTacCaseId(freshdeskTacCaseResponse.getData().getTicket());
        rmaCaseResponseDto.setCaseStatus(CaseStatus.valueOf(rmaCaseTicketResponseDto.getStatusForTickets().name()));
        //fixme: to do this right, we have to do an update to set this field in the RMA Case Record
        rmaCaseResponseDto.setCaseNumber(freshdeskRmaCaseResponse.get().getDisplayId());
        rmaCaseResponseDto.setCaseClosedDate(rmaCaseTicketResponseDto.getStats().getClosedAt());
        rmaCaseResponseDto.setCaseCreatedDate(rmaCaseTicketResponseDto.getCreatedAt());

        return Optional.of(rmaCaseResponseDto);
    }

    //fixme:
    @Override
    public void delete(Long id) {

    }

    //fixme:
    @Override
    public List<RmaCaseResponseDto> listRmaCases(OffsetDateTime caseCreateDateFrom,
                                                 OffsetDateTime caseCreateDateTo,
                                                 OffsetDateTime caseCreateDateSince,
                                                 List<CaseStatus> caseStatus,
                                                 String logic) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Build the query parameters dynamically
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        if (caseCreateDateFrom != null && caseCreateDateTo != null) {
            uriComponentsBuilder.queryParam("created_time[gt]", caseCreateDateFrom.format(formatter))
                    .queryParam("created_time[lt]", caseCreateDateTo.format(formatter));
        } else if (caseCreateDateSince != null) {
            uriComponentsBuilder.queryParam("created_time[gt]", caseCreateDateSince.format(formatter));
        }

        RestClient restClient = snakeCaseRestClient.mutate()
                .baseUrl(restClientConfig.getFreshdeskBaseUri().endsWith("/")
                        ? restClientConfig.getFreshdeskBaseUri()
                        : restClientConfig.getFreshdeskBaseUri() + "/") // Ensure trailing "/"
                .build();

        String schemaId = schemaService.getSchemaIdByName("RMA Cases");
        // Pass the relative path without leading '/' and append the query parameters
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> responseRecords = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("custom_objects/schemas/" + schemaId + "/records") // No leading '/'
                        .query(uriComponentsBuilder.build().getQuery())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        assert responseRecords != null;

        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> freshdeskCaseResponses = responseRecords.getRecords().stream().toList();
        List<RmaCaseResponseDto> rmaCaseResponseDtos = new java.util.ArrayList<>(freshdeskCaseResponses.stream()
                .map(this::mapToRmaCaseResponseDto)
                .toList());

        //fixme: can probably just get rid of this logic check and always assume "AND"
        //fixme: fix this in the controller
        if (caseStatus != null && !caseStatus.isEmpty() && "and".equalsIgnoreCase(logic)) {
            rmaCaseResponseDtos.removeIf(tacCaseResponseDto -> !caseStatus.contains(tacCaseResponseDto.getCaseStatus()));
        }

        // Return the list of records
        return rmaCaseResponseDtos;
    }
    //fixme:
    @Override
    public List<RmaCaseResponseDto> findAll() {
        return List.of();
    }


    //fixme
    @Override
    public boolean exists(Long id) {
        return false;
    }

    @Override
    public RmaCaseAttachmentResponseDto addAttachment(Long ticketId, RmaCaseAttachmentUploadDto uploadDto) throws IOException {

        /*
          Fixme: We should at least validate that the ticket ID sent is a ticket for an RMA???
         */
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
    public List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId) {
        // Retrieve the ticket and its attachments
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);
        List<FreshdeskAttachment> freshdeskAttachments = freshdeskTicketResponseDto.getAttachments();

        // Handle potential null attachments
        if (freshdeskAttachments == null || freshdeskAttachments.isEmpty()) {
            throw new IllegalArgumentException("No attachments found for RMA Case: " + caseId);
        }

        // Map attachments to TacCaseAttachmentResponseDto and collect to a list
        return freshdeskAttachments.stream()
                .map(attachment -> RmaCaseAttachmentResponseDto.builder()
                        .id(attachment.getId())
                        .name(attachment.getName())
                        .mimeType(attachment.getContentType())
                        .description(attachment.getAttachmentUrl())
                        .build())
                .toList();
    }

    @Override
    public RmaCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId) {
        // Retrieve the ticket and its attachments
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);
        if (freshdeskTicketResponseDto == null) {
            throw new IllegalArgumentException("No ticket found for RMA Case: " + caseId);
        }

        List<FreshdeskAttachment> freshdeskAttachments = freshdeskTicketResponseDto.getAttachments();
        if (freshdeskAttachments == null || freshdeskAttachments.isEmpty()) {
            throw new IllegalArgumentException("No attachments found for RMA Case: " + caseId);
        }

        // Filter, map, and return the first matching attachment
        return freshdeskAttachments.stream()
                .filter(attachment -> Objects.equals(attachment.getId(), attachmentId))
                .findFirst()
                .map(attachment -> RmaCaseAttachmentResponseDto.builder()
                        .id(attachment.getId())
                        .name(attachment.getName())
                        .mimeType(attachment.getContentType())
                        .description(attachment.getAttachmentUrl())
                        .build())
                .orElseThrow(() -> new IllegalArgumentException("No attachment found for RMA Case: " + caseId + " with attachment ID: " + attachmentId));
    }

    @Override
    public RmaCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId) {
        return null;
    }

    @Override
    public void deleteAttachment(Long caseId, Long attachmentId) {

    }

    @Override
    public void deleteAllAttachments(Long caseId) {

    }

    @Override
    public RmaCaseNoteResponseDto addNote(Long ticketId, RmaCaseNoteUploadDto uploadDto) throws IOException {

        FreshdeskTicketCreateNoteDto dto = FreshdeskTicketCreateNoteDto.builder()
                .body(uploadDto.getText())
                .privateField(false)
                .incoming(true)
                .build();

        FreshdeskTicketNoteResponseDto freshdeskTicketNoteDto = createFreshdeskTicketNote(dto, ticketId);

        return RmaCaseNoteResponseDto.builder()
                .id(freshdeskTicketNoteDto.getId())
                .author(uploadDto.getAuthor())
                .rmaCaseId(ticketId)
                .date(freshdeskTicketNoteDto.getCreatedAt())
                .build();
    }

    @Override
    public List<RmaCaseNoteResponseDto> getAllNotes(Long caseId) {

        //first get all the notes of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        return freshdeskTicketConversations.stream()
                .filter(freshdesk -> freshdesk.getSource() == FreshdeskConversationSource.Note)
                .map(freshdesk -> RmaCaseNoteResponseDto.builder()
                        .id(freshdesk.getId())
                        .rmaCaseId(caseId)
                        .author("FD User ID:"+freshdesk.getUserId().toString())
                        .date(freshdesk.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public RmaCaseNoteDownloadDto getNote(Long caseId, Long noteId) {
        // Retrieve all the conversations of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        // Find and map the note to TacCaseNoteDownloadDto
        return freshdeskTicketConversations.stream()
                .filter(freshdesk -> Objects.equals(freshdesk.getId(), noteId))
                .map(freshdesk -> RmaCaseNoteDownloadDto.builder()
                        .id(freshdesk.getId())
                        .rmaCaseId(caseId)
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

    private FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseRecordsByTicketId(Long tacTicketId) {
        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records?ticket={ticketId}", tacCaseSchemaId, tacTicketId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> findFreshdeskRmaCaseRecordsByTicketId(Long rmaTicketId) {
        String tacCaseSchemaId = schemaService.getSchemaIdByName("RMA Cases");
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseResponseDto = snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records?ticket={ticketId}", tacCaseSchemaId, rmaTicketId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return rmaCaseResponseDto;
    }

    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseByDisplayId(String displayId) {
        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", tacCaseSchemaId, displayId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskRmaCaseResponseDto findFreshdeskRmaCaseByTicketId(Long id) {
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> freshdeskRmaCaseRecords = findFreshdeskRmaCaseRecordsByTicketId(id);
        Optional<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> record = freshdeskRmaCaseRecords.getRecords().stream().findFirst();
        FreshdeskRmaCaseResponseDto freshdeskRmaCaseResponseDto = record.map(FreshdeskCaseResponse::getData).orElse(null);
        FreshdeskRmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(freshdeskRmaCaseResponseDto, FreshdeskRmaCaseResponseDto.class);
        rmaCaseResponseDto.setCaseNumber(record.stream().findFirst().get().getDisplayId()); //fixme: 'Optional. get()' without 'isPresent()' check
        return rmaCaseResponseDto;
    }


    private FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> findFreshdeskRmaCaseByDisplayId(String displayId) {
        String rmaCaseSchemaId = schemaService.getSchemaIdByName("RMA Cases");
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", rmaCaseSchemaId, displayId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskTicketResponseDto findFreshdeskTicketById(Long id) {
        return snakeCaseRestClient.get()
                .uri("/tickets/{id}?include=stats", id)
                .retrieve()
                .body(FreshdeskTicketResponseDto.class);
    }

    //fixme: a lot of duplicate code in this class and sister TAC Case Service class
    private FreshdeskTicketResponseDto createFreshdeskTicket(FreshdeskTicketCreateDto dto) {
        return snakeCaseRestClient.post()
                .uri("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(FreshdeskTicketResponseDto.class);
    }

    private FreshdeskTicketCreateDto buildCreateChildTicketDto(FreshdeskTicketResponseDto tacCaseTicketResponseDto, RmaCaseCreateDto rmaCaseCreateDto) {

        return FreshdeskTicketCreateDto.builder()
                .email(rmaCaseCreateDto.getContactEmail())
                .subject("RMA: "+ tacCaseTicketResponseDto.getSubject())
                .responderId(tacCaseTicketResponseDto.getResponderId())
                .type("Problem")
                .source(Source.Email)
                .status(StatusForTickets.Open)
                .priority(tacCaseTicketResponseDto.getPriorityForTickets())
                .description("RMA for TAC Case: "+rmaCaseCreateDto.getTacCaseId())
                .parentId(tacCaseTicketResponseDto.getId())
                .build();
    }

    private FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> updateRmaCase(
            String rmaCaseDisplayId, FreshdeskRmaCaseUpdateRequest request) {

        String rmaCaseSchemaId = schemaService.getSchemaIdByName("RMA Cases");
        return snakeCaseRestClient.put()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", rmaCaseSchemaId, rmaCaseDisplayId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
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

    private RmaCaseAttachmentResponseDto mapToAttachmentResponse(FreshdeskTicketResponseDto ticketResponseDto, String fileName) {
        // Find the attachment matching the fileName
        FreshdeskAttachment freshdeskAttachment = ticketResponseDto.getAttachments().stream()
                .filter(attachment -> attachment.getName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attachment with name '" + fileName + "' not found"));

        // Map the FreshdeskAttachment to TacCaseAttachmentResponseDto
        return RmaCaseAttachmentResponseDto.builder()
                .id(freshdeskAttachment.getId())
                .mimeType(freshdeskAttachment.getContentType())
                .name(freshdeskAttachment.getName())
                .description(freshdeskAttachment.getAttachmentUrl())
                .size(freshdeskAttachment.getSize().floatValue())
                .build();
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

    private RmaCaseResponseDto mapToRmaCaseResponseDto(FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> freshdeskRmaCaseResponse) {

        FreshdeskRmaCaseResponseDto freshdeskRmaCaseResponseDto = freshdeskRmaCaseResponse.getData();
        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(freshdeskRmaCaseResponseDto, RmaCaseResponseDto.class);
        rmaCaseResponseDto.setCaseNumber(freshdeskRmaCaseResponse.getDisplayId());

        Long rmaTicketId = freshdeskRmaCaseResponseDto.getTicket();
        //fixme: someday we need to fix this because this will be costly to the responsiveness of the API
        //need to get the ticket for each of the responses to set
        if (rmaTicketId != null) {
            rmaCaseResponseDto.setId(rmaTicketId);
            FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(rmaTicketId);
            List<Long> associatedTicketsList = freshdeskTicketResponseDto.getAssociatedTicketsList();
            if (associatedTicketsList != null && associatedTicketsList.size() == 1) {
                rmaCaseResponseDto.setTacCaseId(associatedTicketsList.getFirst());
            }
            rmaCaseResponseDto.setCaseStatus(CaseStatus.valueOf(freshdeskTicketResponseDto.getStatusForTickets().name()));
            rmaCaseResponseDto.setCaseClosedDate(freshdeskTicketResponseDto.getStats().getClosedAt());
            rmaCaseResponseDto.setCaseCreatedDate(freshdeskTicketResponseDto.getCreatedAt());
        }

        return rmaCaseResponseDto;
    }

    private static Long extractRmaTicketNumberFromKey(String input) {
        Pattern pattern = Pattern.compile("RMA:(\\d+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null; // Return null if "RMA:" is not found
    }


}
