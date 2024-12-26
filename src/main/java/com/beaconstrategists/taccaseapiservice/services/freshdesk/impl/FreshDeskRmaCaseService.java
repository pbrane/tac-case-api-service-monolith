package com.beaconstrategists.taccaseapiservice.services.freshdesk.impl;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.beaconstrategists.taccaseapiservice.dtos.freshdesk.*;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.FieldPresenceModelMapper;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.GenericModelMapper;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.CompanyService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.SchemaService;
import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.services.RmaCaseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service("FreshdeskRmaCaseService")
public class FreshDeskRmaCaseService implements RmaCaseService {

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

    @Value("${FD_CUSTOMER_NAME:Beacon}")
    private String companyName;

    @Value("${FD_DEFAULT_RESPONDER_ID:3043029172572}")
    private String defaultResponderId;

    @Value("${FD_RMA_CASE_ID_PREFIX:_3-}")
    private String rmaCaseIdPrefix;

    private final SchemaService schemaService;

    public FreshDeskRmaCaseService(@Qualifier("snakeCaseRestClient") RestClient snakeCaseRestClient,
                                   SchemaService schemaService,
                                   CompanyService companyService,
                                   GenericModelMapper genericModelMapper,
                                   @Qualifier("fieldPresenceSnakeCaseSerializingRestClient") RestClient fieldPresenseRestClient) {

        this.snakeCaseRestClient = snakeCaseRestClient;
        this.companyService = companyService;
        this.genericModelMapper = genericModelMapper;
        this.schemaService = schemaService;
        this.fieldPresenseRestClient = fieldPresenseRestClient;
    }


    /*
     * Create an RMA Case in Freshdesk
     */

    @Override
    public RmaCaseResponseDto create(RmaCaseCreateDto rmaCaseCreateDto) {

        //Find the TAC Case
        Long id = rmaCaseCreateDto.getTacCaseId();
        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
        FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> freshdeskCaseResponseRecords = findFreshdeskTacCaseRecordsByTicketId(id, tacCaseSchemaId);
        assert freshdeskCaseResponseRecords != null;

        Optional<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> record = freshdeskCaseResponseRecords.getRecords().stream().findFirst();
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = record.map(FreshdeskCaseResponse::getData).orElse(null);
        //get the record's identifier, this is what we need to update the record
        String tacCaseDisplayId = record.get().getDisplayId();
        Long ticketId = freshdeskTacCaseResponseDto.getTicket();
        String rmaKey = freshdeskTacCaseResponseDto.getKey();

        //Create the RMA Case referring to the TAC Case
        FreshdeskRmaCaseCreateDto freshdeskRmaCaseCreateDto = genericModelMapper.map(rmaCaseCreateDto, FreshdeskRmaCaseCreateDto.class);
        Random random = new Random();
        char[] alphabet = {'m','o','l','e','x','m','s','f','t','2','0','2','5'};
        int size = 4;
        String randomKey = NanoIdUtils.randomNanoId(random, alphabet, size);
        freshdeskRmaCaseCreateDto.setKey(rmaKey+"; RMA: "+ randomKey);
        freshdeskRmaCaseCreateDto.setTacCase(tacCaseDisplayId);
        FreshdeskDataCreateRequest<FreshdeskRmaCaseCreateDto> freshdeskRmaCreateRequest = new FreshdeskDataCreateRequest<>(freshdeskRmaCaseCreateDto);
        String rmaCaseSchemaId = schemaService.getSchemaIdByName("RMA Cases");

        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> createRmaCaseResponse = snakeCaseRestClient.post()
                .uri("/custom_objects/schemas/{schemaId}/records", rmaCaseSchemaId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(freshdeskRmaCreateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        assert createRmaCaseResponse != null;

        Long rmaId = parseIdFromDisplayId(createRmaCaseResponse.getDisplayId(), rmaCaseIdPrefix);
        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(createRmaCaseResponse.getData(), RmaCaseResponseDto.class);
        rmaCaseResponseDto.setId(rmaId);
        rmaCaseResponseDto.setTacCaseId(ticketId);

        //fixme: to do this right, we have to to an update to set this field
//        responseDto.setCaseNumber(createRmaCaseResponse.getDisplayId());

        return rmaCaseResponseDto;
    }

    @Override
    public RmaCaseResponseDto update(Long id, RmaCaseUpdateDto rmaCaseUpdateDto) {

        //fixme: need to get the TAC Case for this RMA Case in order to return the tacCaseId in the Response
        //fixme: so, go ahead and fetch the RMA Case to get the display_id of the TAC Case, then fetch
        //fixme: the TAC Case to get the TicketID
        //
        //fixme: a better solution just might be to store those display_ids in the caseNumber (shrug)


        //find the RMA
        //the FD ID is the display ID which is the RMA Prefix + the ID as a String
        String rmaDisplayId = rmaCaseIdPrefix+id;

        //First, get the existing record
        //"/api/v2/custom_objects/schemas/{schema-id}/records/{record-id}"
        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> freshdeskRmaCaseResponse = getFreshdeskRmaCaseResponse(id);

        assert freshdeskRmaCaseResponse != null;

        String rmaCaseDisplayId = freshdeskRmaCaseResponse.getDisplayId();
        FreshdeskRmaCaseResponseDto freshdeskRmaCaseResponseDto = freshdeskRmaCaseResponse.getData();

        //get the TacCase to get the Ticket ID to save in the RMA Update Response as tacCaseId
        String tacCaseDisplayId = freshdeskRmaCaseResponseDto.getTacCase();
        assert tacCaseDisplayId != null;

        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse =
                findFreshdeskTacCaseByDisplayId(tacCaseDisplayId);

        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = freshdeskTacCaseResponse.getData();
        assert freshdeskTacCaseResponseDto != null;

        Long tacCaseId = freshdeskTacCaseResponseDto.getTicket();

        FreshdeskRmaCaseUpdateDto freshdeskRmaCaseUpdateDto = genericModelMapper.
                map(freshdeskRmaCaseResponseDto, FreshdeskRmaCaseUpdateDto.class);

        //Map the changes from the update request to the existing data
        FieldPresenceModelMapper fieldPresenceModelMapper = new FieldPresenceModelMapper();
        fieldPresenceModelMapper.map(rmaCaseUpdateDto, freshdeskRmaCaseUpdateDto);

        //Send the update
        FreshdeskRmaCaseUpdateRequest freshdeskRmaCaseUpdateRequest = new FreshdeskRmaCaseUpdateRequest(freshdeskRmaCaseUpdateDto);
        freshdeskRmaCaseUpdateRequest.setDisplayId(freshdeskRmaCaseResponse.getDisplayId());
        freshdeskRmaCaseUpdateRequest.setVersion(freshdeskRmaCaseResponse.getVersion());

        String rmaCaseSchemaId = schemaService.getSchemaIdByName("RMA Cases");

        freshdeskRmaCaseResponse = snakeCaseRestClient.put()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", rmaCaseSchemaId, rmaDisplayId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(freshdeskRmaCaseUpdateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        assert freshdeskRmaCaseResponse != null;


        freshdeskRmaCaseResponseDto = freshdeskRmaCaseResponse.getData();
/*
        Note:
        This genericModelMapper returns an instance of FreshdeskRmaCaseResponseDto
        Really weird but fortunately, it works, the extra few fields added to the child class
        are not serialized in the response from the controller
*/
        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(freshdeskRmaCaseResponseDto, RmaCaseResponseDto.class);
        Long rmaId = parseIdFromDisplayId(freshdeskRmaCaseResponse.getDisplayId(), rmaCaseIdPrefix);
        rmaCaseResponseDto.setId(rmaId);
        rmaCaseResponseDto.setTacCaseId(tacCaseId); //tricky part

        return rmaCaseResponseDto;
    }


    @Override
    public Optional<RmaCaseResponseDto> findById(Long id) {

        //fixme: duplicate code fragment

        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> freshdeskRmaCaseResponse = getFreshdeskRmaCaseResponse(id);
        FreshdeskRmaCaseResponseDto freshdeskRmaCaseResponseDto = freshdeskRmaCaseResponse.getData();
        assert freshdeskRmaCaseResponseDto != null;

        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(freshdeskRmaCaseResponseDto, RmaCaseResponseDto.class);

        //now we have to pull the TAC Case to get the TAC Case ID which is the Freshdesk Ticket ID
        FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponse =
                findFreshdeskTacCaseByDisplayId(freshdeskRmaCaseResponseDto.getTacCase());
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = freshdeskTacCaseResponse.getData();
        assert freshdeskTacCaseResponseDto != null;

        rmaCaseResponseDto.setId(id);
        rmaCaseResponseDto.setTacCaseId(freshdeskTacCaseResponseDto.getTicket());

        return Optional.of(rmaCaseResponseDto);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<RmaCaseResponseDto> listRmaCases(OffsetDateTime caseCreateDateFrom,
                                                 OffsetDateTime caseCreateDateTo,
                                                 OffsetDateTime caseCreateDateSince,
                                                 List<CaseStatus> caseStatus,
                                                 String logic) {



        return List.of();
    }

    @Override
    public List<RmaCaseResponseDto> findAll() {
        return List.of();
    }

    @Override
    public boolean exists(Long id) {
        return false;
    }

    @Override
    public RmaCaseAttachmentResponseDto addAttachment(Long caseId, RmaCaseAttachmentUploadDto uploadDto) throws IOException {
        return null;
    }

    @Override
    public List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId) {
        return List.of();
    }

    @Override
    public RmaCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId) {
        return null;
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
    public RmaCaseNoteResponseDto addNote(Long caseId, RmaCaseNoteUploadDto uploadDto) throws IOException {
        return null;
    }

    @Override
    public List<RmaCaseNoteResponseDto> getAllNotes(Long caseId) {
        return List.of();
    }

    @Override
    public RmaCaseNoteDownloadDto getNote(Long caseId, Long noteId) {
        return null;
    }

    @Override
    public void deleteNote(Long caseId, Long noteId) {

    }

    @Override
    public void deleteAllNotes(Long caseId) {

    }


    /*
    Helper Methods
     */

    private static Long parseIdFromDisplayId(String displayId, String rmaCaseIdPrefix) {
        Long rmaId;
        if (displayId.contains(rmaCaseIdPrefix)) {
            String value = displayId.substring(displayId.indexOf(rmaCaseIdPrefix) + rmaCaseIdPrefix.length());
            rmaId = Long.valueOf(value);
            System.out.println("Extracted Integer: " + rmaId);
        } else {
            System.out.println("Prefix not found");
            throw new IllegalStateException("Prefix: { "+rmaCaseIdPrefix+" } not found.");
        }

        return rmaId;
    }

    private FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseRecordsByTicketId(Long id, String tacCaseSchemaId) {
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records?ticket={ticketId}", tacCaseSchemaId, id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseByDisplayId(String displayId) {
        String tacCaseSchemaId = schemaService.getSchemaIdByName("TAC Cases");
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", tacCaseSchemaId, displayId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskTicketResponseDto findFreshdeskTicketById(Long id) {
        return snakeCaseRestClient.get()
                .uri("/tickets/{id}", id)
                .retrieve()
                .body(FreshdeskTicketResponseDto.class);
    }

    private FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> getFreshdeskRmaCaseResponse(Long id) {
        String rmaDisplayId = rmaCaseIdPrefix+ id;
        String rmaSchemaId = schemaService.getSchemaIdByName("RMA Cases");
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records/{record-id}", rmaSchemaId, rmaDisplayId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }



}
