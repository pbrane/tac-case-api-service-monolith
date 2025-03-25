package com.beaconstrategists.taccaseapiservice.services.freshdesk.impl;

import com.beaconstrategists.taccaseapiservice.config.freshdesk.RestClientConfig;
import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.dtos.freshdesk.*;
import com.beaconstrategists.taccaseapiservice.exceptions.ResourceNotFoundException;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.FieldPresenceModelMapper;
import com.beaconstrategists.taccaseapiservice.mappers.freshdesk.GenericModelMapper;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.FreshdeskConversationSource;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.Source;
import com.beaconstrategists.taccaseapiservice.model.freshdesk.StatusForTickets;
import com.beaconstrategists.taccaseapiservice.services.RmaCaseService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.SchemaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Value("${RMA_DEFAULT_SHIPPED_CARRIER:FedEx}")
    private String defaultShippedCarrier;

    @Value("${ESCAPE_HTML_DESCRIPTION:true}")
    private boolean escapeHtmlDescription;

    //this value is used by the deserializer, if this is true, the other escapes can be ignored
    @Value("${ESCAPE_HTML_STRINGS:false}")
    private boolean escapeHtmlStrings;

    private final static int FD_TICKET_QUERY_PAGE_SIZE = 30;
    private final static int FD_TICKET_QUERY_MAX_PAGES = 10;


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
        FreshdeskTicketResponseDto createRmaCaseTicketResponseDto = createFreshdeskTicket(rmaTicketCreateDto);

        /*
         * We've created the child Ticket for the RMA, now create the RMA Case
         * and associate it with the child Ticket and the TAC Case
         */

        /*
         * Get the TAC Case
         */
        FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> freshdeskTacCaseResponseRecords = findFreshdeskTacCaseRecordsByTicketId(rmaCaseCreateDto.getTacCaseId());
        assert freshdeskTacCaseResponseRecords != null;

        Optional<FreshdeskCaseResponse<FreshdeskTacCaseResponseDto>> tacCaseRecord = freshdeskTacCaseResponseRecords.getRecords().stream().findFirst();
        FreshdeskTacCaseResponseDto freshdeskTacCaseResponseDto = tacCaseRecord.map(FreshdeskCaseResponse::getData).orElse(null);

        FreshdeskRmaCaseCreateDto freshdeskRmaCaseCreateDto = genericModelMapper.map(rmaCaseCreateDto, FreshdeskRmaCaseCreateDto.class);
        if (rmaCaseCreateDto.getShippedCarrier() == null) {
            freshdeskRmaCaseCreateDto.setShippedCarrier(defaultShippedCarrier);
        }
        assert freshdeskTacCaseResponseDto != null;

        freshdeskRmaCaseCreateDto.setKey("RMA:"+createRmaCaseTicketResponseDto.getId()+", "+ freshdeskTacCaseResponseDto.getKey());
        freshdeskRmaCaseCreateDto.setTacCase(tacCaseRecord.get().getDisplayId());
        freshdeskRmaCaseCreateDto.setTicket(createRmaCaseTicketResponseDto.getId());

        FreshdeskDataCreateRequest<FreshdeskRmaCaseCreateDto> freshdeskRmaCreateRequest = new FreshdeskDataCreateRequest<>(freshdeskRmaCaseCreateDto);
        String rmaCaseSchemaId = schemaService.getRMACaseSchemaId();

        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> createRmaCaseResponse = createRmaCase(rmaCaseSchemaId, freshdeskRmaCreateRequest);
        assert createRmaCaseResponse != null;

        String rmaDisplayId = createRmaCaseResponse.getDisplayId();

        RmaCaseResponseDto rmaCaseResponseDto = genericModelMapper.map(createRmaCaseResponse.getData(), RmaCaseResponseDto.class);
        rmaCaseResponseDto.setId(createRmaCaseTicketResponseDto.getId());
        rmaCaseResponseDto.setTacCaseId(rmaCaseCreateDto.getTacCaseId());
        rmaCaseResponseDto.setCaseStatus(CaseStatus.valueOf(createRmaCaseTicketResponseDto.getStatusForTickets().name()));
        rmaCaseResponseDto.setProblemDescription(createRmaCaseTicketResponseDto.getDescriptionText());
        //fixme: to do this right, we have to do an update to set this field in the RMA Case Record
        //fixme: or just never save it in the record. Just always get it from the displayId???
        rmaCaseResponseDto.setCaseNumber(rmaDisplayId);
        rmaCaseResponseDto.setCaseCreatedDate(createRmaCaseTicketResponseDto.getCreatedAt());

        return rmaCaseResponseDto;
    }

    @Override
    public RmaCaseResponseDto update(Long caseId, RmaCaseUpdateDto rmaCaseUpdateDto) {

        //First make sure this is a valid RMA Case
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();
        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        //fixme: there may be other fields for the ticket that should be updated,
        //fixme: for example, problem description
        /*
         * If the update contains caseStatus, we should also update the ticket
         * Otherwise, just fetch the ticket.
         */
        FreshdeskTicketUpdateDto updateTicketDto = new FreshdeskTicketUpdateDto();
        FreshdeskTicketResponseDto rmaCaseUpdateTicketResponseDto = null;
        boolean updateTicket = false;
        if (rmaCaseUpdateDto.isFieldPresent("caseStatus")) {
            updateTicketDto.setStatus(StatusForTickets.valueOf(rmaCaseUpdateDto.getCaseStatus().getValue()));
            updateTicket = true;
        }

        if (rmaCaseUpdateDto.isFieldPresent("problemDescription")) {
            String problemDescription = rmaCaseUpdateDto.getProblemDescription();
            //problemDescription can be included in update and still be null
            if (problemDescription != null && escapeHtmlDescription && !escapeHtmlStrings) {
                problemDescription = HtmlUtils.htmlEscape(problemDescription);
            }
            updateTicketDto.setDescription(problemDescription);
            updateTicket = true;
        }

        if (updateTicket) {
            rmaCaseUpdateTicketResponseDto = updateTicket(caseId, updateTicketDto);
        }

        /*
         * Always do the find so we can use the stats metadata in the final Response DTO
         */
        FreshdeskTicketResponseDto rmaCaseTicketResponseDto = findFreshdeskTicketById(caseId);

        /*
         * Now, we need to find the RMA Case Record associated with this ticket ID
         */
/*
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecords =
                findFreshdeskRmaCaseRecordsByTicketId(caseId);
        Optional<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> freshdeskRmaCaseResponse =
                rmaCaseRecords.getRecords().stream().findFirst();
*/
        Optional<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> record = records.stream().findFirst();
        FreshdeskRmaCaseResponseDto findRmaCaseResponseDto =
                record.map(FreshdeskCaseResponse::getData).orElse(null);
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
        freshdeskRmaCaseUpdateRequest.setDisplayId(record.get().getDisplayId());
        freshdeskRmaCaseUpdateRequest.setVersion(record.get().getVersion());
        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> rmaCaseUpdateResponse =
                updateRmaCase(record.get().getDisplayId(), freshdeskRmaCaseUpdateRequest);
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
        rmaCaseResponseDto.setCaseNumber(record.get().getDisplayId());
        rmaCaseResponseDto.setCaseClosedDate(rmaCaseTicketResponseDto.getStats().getClosedAt());
        rmaCaseResponseDto.setProblemDescription(rmaCaseTicketResponseDto.getDescriptionText());
        rmaCaseResponseDto.setCaseCreatedDate(rmaCaseTicketResponseDto.getCreatedAt());

        return rmaCaseResponseDto;
    }

    @Override
    public Optional<RmaCaseResponseDto> findById(Long rmaTicketId) {
        return findFreshdeskRmaCaseRecordsByTicketId(rmaTicketId)
                .getRecords()
                .stream()
                .findFirst()
                .map(FreshdeskCaseResponse::getData)
                .flatMap(rmaCaseData -> {
                    // Fetch RMA Ticket
                    Optional<FreshdeskTicketResponseDto> rmaTicket = Optional.ofNullable(findFreshdeskTicketById(rmaTicketId));

                    // Fetch TAC Case linked to this RMA case
                    Optional<FreshdeskTacCaseResponseDto> tacCase = Optional.ofNullable(findFreshdeskTacCaseByDisplayId(rmaCaseData.getTacCase()))
                            .map(FreshdeskCaseResponse::getData);

                    return rmaTicket.flatMap(ticket -> tacCase.map(tacData -> mapToRmaCaseResponseDto(ticket, rmaCaseData, tacData)));
                });
    }


    //fixme:
    @Override
    public void delete(Long id) {

    }


    @Override
    public List<RmaCaseResponseDto> listRmaCases(OffsetDateTime caseCreateDateFrom,
                                                 OffsetDateTime caseCreateDateTo,
                                                 OffsetDateTime caseCreateDateSince,
                                                 List<CaseStatus> caseStatus,
                                                 String logic,
                                                 Integer pageSize,
                                                 Integer pageLimit) {

        if ((caseCreateDateFrom != null && caseCreateDateTo != null) || (caseCreateDateSince != null)) {
            return listRmaCasesQuery(caseCreateDateFrom, caseCreateDateTo, caseCreateDateSince, caseStatus, logic, pageSize, pageLimit);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String schemaId = schemaService.getRMACaseSchemaId();

        //Mutating this client to adapt to path creation due to using the UriComponents builder
        //This forces a trailing "/"
        RestClient restClient = mutateRestClient();

        // Build the query parameters dynamically
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.queryParam("page_size", pageSize);

        /*
         * Saving this for posterity even though it's not functional, now.
        if (caseCreateDateFrom != null && caseCreateDateTo != null) {
            uriComponentsBuilder.queryParam("created_time[gte]", caseCreateDateFrom.format(formatter))
                    .queryParam("created_time[lt]", caseCreateDateTo.format(formatter));
        } else if (caseCreateDateSince != null) {
            uriComponentsBuilder.queryParam("created_time[gte]", caseCreateDateSince.format(formatter));
        }
         */

        // Initialize result list
        List<RmaCaseResponseDto> rmaCaseResponseDtos = new ArrayList<>();
        String query = uriComponentsBuilder.build().getQuery();

        /*
         * Don't include the leading slash in this URL String as it will
         * cause the path ("/api/v2") already included in the REST Client to be trimmed off.
         */
        String nextPageUrl = "custom_objects/schemas/" + schemaId + "/records?" + query;

        int pageCount = 0;
        do {
            pageCount++;
            FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> responseRecords = searchCaseRecords(restClient, nextPageUrl);

            if (responseRecords == null || responseRecords.getRecords() == null) {
                break; // No more records
            }

            // Convert and add records
            List<RmaCaseResponseDto> currentBatch = responseRecords.getRecords().stream()
                    .map(this::mapToRmaCaseResponseDto)
                    .toList();
            rmaCaseResponseDtos.addAll(currentBatch);

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
        //fixme: can probably just get rid of this logic check and always assume "AND"
        //fixme: fix this in the controller
        if (caseStatus != null && !caseStatus.isEmpty() && "and".equalsIgnoreCase(logic)) {
            rmaCaseResponseDtos.removeIf(rmaCaseResponseDto -> !caseStatus.contains(rmaCaseResponseDto.getCaseStatus()));
        }

        // Return the list of records
        return rmaCaseResponseDtos;
    }

    private static FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> searchCaseRecords(RestClient restClient, String nextPageUrl) {
        return restClient
                .get()
                .uri(nextPageUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /*
     * Called from the API if the listing contains query parameters.
     */
    public List<RmaCaseResponseDto> listRmaCasesQuery(final OffsetDateTime caseCreateDateFrom,
                                                      final OffsetDateTime caseCreateDateTo,
                                                      final OffsetDateTime caseCreateDateSince,
                                                      final List<CaseStatus> caseStatus,
                                                      final String logic,
                                                      final Integer pageSize,      // Freshdesk Search always returns 30 per page, but we’ll keep this param for consistency
                                                      final Integer pageLimit) {

        //Mutating this client to adapt to path creation due to using the UriComponents builder
        //This forces a trailing slash "/" in the URI
        RestClient restClient = mutateRestClient();
        List<RmaCaseResponseDto> results = new ArrayList<>();
        Set<Long> seenCaseIds = new HashSet<>();

        //Created these twin fields to generically support a caseCreatedDateSince query
        OffsetDateTime createDateFrom;
        OffsetDateTime createDateTo;
        if (caseCreateDateSince != null) {
            createDateFrom = caseCreateDateSince;
            createDateTo = OffsetDateTime.now();
        } else {
            createDateFrom = caseCreateDateFrom;
            createDateTo = caseCreateDateTo;
        }

        String filterClause = createFilterClause(createDateFrom, createDateTo);
        int page = 1;
        FreshdeskTicketSearchResponseDto ticketSearchResponse = searchTickets(restClient, getRelativeUri(filterClause, page));

        int totalTicketsFound = ticketSearchResponse.getTotal();
        int totalPages = (totalTicketsFound / FD_TICKET_QUERY_PAGE_SIZE) + (totalTicketsFound % FD_TICKET_QUERY_PAGE_SIZE == 0 ? 0 : 1);
        int pageBundleSize = FD_TICKET_QUERY_MAX_PAGES;
        int totalPageBundles = (totalPages / pageBundleSize) + (totalPages % pageBundleSize == 0 ? 0 : 1);

        OffsetDateTime caseCreateDateToTracker = createDateFrom;
        int pageBundle = 1;
        int totalPageCnt = 1;
        while (pageBundle <= totalPageBundles) {

            //fixme: need to stop if we've exceeded the number of pages needed within the bundle
            while (totalPageCnt <= pageLimit && page <= FD_TICKET_QUERY_MAX_PAGES) {

                //we have to repeat the search first time through, unfortunately
                ticketSearchResponse = searchTickets(restClient, getRelativeUri(filterClause, page));

                // work through this page of responses
                for (FreshdeskTicketResponseDto ticket : ticketSearchResponse.getResults()) {
                    caseCreateDateToTracker = ticket.getCreatedAt();

                    if (seenCaseIds.contains(ticket.getId())) {
                        continue;
                    } else {
                        seenCaseIds.add(ticket.getId());
                    }

                    FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> caseRecords = findFreshdeskRmaCaseRecordsByTicketId(ticket.getId());
                    // Typically, 1 record for that ticket. If found, map them:
                    Optional<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> rmaCase = caseRecords.getRecords().stream().findFirst();

                    //Do I really need this if statement?
                    if (rmaCase.isPresent()) {
                        FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> rmaCaseResponse = rmaCase.get();
                        // fixme: someday - This is unfortunate that we have to query the same ticket again to get the stats
                        FreshdeskTicketResponseDto ticketWithStats = findFreshdeskTicketById(ticket.getId());
                        RmaCaseResponseDto dto = mapToRmaCaseResponseDto(rmaCaseResponse);
                        results.add(dto);
                        seenCaseIds.add(dto.getId());
                    }
                    else {
                        // Log something here
                    }
                }
                //we need to do this up top???
                page++;
                totalPageCnt++;
            }

            filterClause = createFilterClause(createDateFrom, caseCreateDateToTracker);
            page=1;
            pageBundle++;
        }

        results.removeIf(rma -> rma.getCaseCreatedDate().isBefore(createDateFrom) || rma.getCaseCreatedDate().isAfter(createDateTo));

        if (caseStatus != null && !caseStatus.isEmpty() && "and".equalsIgnoreCase(logic)) {
            results.removeIf(rma -> !caseStatus.contains(rma.getCaseStatus()));
        }

        return results;
    }

    private RestClient mutateRestClient() {
        return snakeCaseRestClient.mutate()
                .baseUrl(restClientConfig.getFreshdeskBaseUri().endsWith("/")
                        ? restClientConfig.getFreshdeskBaseUri()
                        : restClientConfig.getFreshdeskBaseUri() + "/") // Ensure trailing "/"
                .build();
    }

    private static String createFilterClause(OffsetDateTime caseCreateDateFrom, OffsetDateTime caseCreateDateTo) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        String filterClause = "";
        filterClause = String.format("created_at:>'%s' AND created_at:<'%s' AND tag:'%s'", caseCreateDateFrom.format(fmt), caseCreateDateTo.format(fmt), "RMA");
        //filterClause = String.format("created_at:>'%s' AND created_at:<'%s'", caseCreateDateFrom.format(fmt), caseCreateDateTo.format(fmt));
        filterClause = "\"" + filterClause + "\"";
        return filterClause;
    }

    private static FreshdeskTicketSearchResponseDto searchTickets(RestClient restClient, URI relativeUri) {
        FreshdeskTicketSearchResponseDto ticketSearchResponse =
                restClient.get()
                        .uri(relativeUri)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });
        return ticketSearchResponse;
    }

    private static URI getRelativeUri(String finalQuery, int page) {
        UriComponents uc = UriComponentsBuilder.newInstance()
                // Use .path("search/tickets"), NOT .pathSegment("search", "tickets")
                // this prevents the truncation of any path already set in the baseUrl
                // of the RestClient by not prepending a "/" to the relative path
                // this implies that the RestClient as been mutated to have a "/" appended
                // to the baseUri
                .path("search/tickets")
                .queryParam("query", finalQuery)
                .queryParam("page", page)
                .build()
                .encode();  // single-pass encoding
        URI relativeUri = uc.toUri();  // "search/tickets?query=...&page=1" (no scheme/host)
        return relativeUri;
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
    public RmaCaseAttachmentResponseDto addAttachment(Long caseId, RmaCaseAttachmentUploadDto uploadDto) throws IOException {

        /*
          Fixme: We should at least validate that the ticket ID sent is a ticket for an RMA???
         */
        MultipartFile file = uploadDto.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must be provided and not empty");
        }

        validateFileType(file);

        //First make sure this is a valid RMA Case
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();
        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
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
    public List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId) {
        //First make sure this is a valid RMA Case
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

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

        //First make sure this is a valid RMA Case
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        // Retrieve the ticket and its attachments
        FreshdeskTicketResponseDto freshdeskTicketResponseDto = findFreshdeskTicketById(caseId);
        if (freshdeskTicketResponseDto == null) {
            throw new ResourceNotFoundException("No required ticket found for RMA Case: " + caseId, "INVALID_CASE");
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
    public RmaCaseNoteResponseDto addNote(Long caseId, RmaCaseNoteUploadDto uploadDto) throws IOException {

        //First make sure this is a valid RMA Ticket
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        FreshdeskTicketCreateNoteDto dto = FreshdeskTicketCreateNoteDto.builder()
                .body(uploadDto.getText())
                .privateField(false)
                .incoming(true)
                .build();

        FreshdeskTicketNoteResponseDto freshdeskTicketNoteDto = createFreshdeskTicketNote(dto, caseId);

        return RmaCaseNoteResponseDto.builder()
                .id(freshdeskTicketNoteDto.getId())
                .author(uploadDto.getAuthor())
                .rmaCaseId(caseId)
                .date(freshdeskTicketNoteDto.getCreatedAt())
                .build();
    }

    @Override
    public List<RmaCaseNoteResponseDto> getAllNotes(Long caseId) {

        //First make sure this is a valid RMA Ticket
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        //first get all the notes of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        return freshdeskTicketConversations.stream()
                .filter(freshdesk -> freshdesk.getSource() == FreshdeskConversationSource.Note)
                .filter(freshdesk -> !freshdesk.isPrivate())
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

        //First make sure this is a valid RMA Ticket
        FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> rmaCaseRecordsByTicketId = findFreshdeskRmaCaseRecordsByTicketId(caseId);
        List<FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto>> records = rmaCaseRecordsByTicketId.getRecords();

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Cannot retrieve case results: Invalid or Missing Case Number.", "INVALID_CASE");
        }

        // Retrieve all the conversations of a ticket
        List<FreshdeskTicketConversationDto> freshdeskTicketConversations = findFreshdeskTicketConversations(caseId);

        // Find and map the note to RmaCaseNoteDownloadDto
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No note found with noteId: " + noteId + " for Case ID: " + caseId, "INVALID_CASE"));
    }

    @Override
    public void deleteNote(Long caseId, Long noteId) {

    }

    @Override
    public void deleteAllNotes(Long caseId) {

    }

    private FreshdeskCaseResponseRecords<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseRecordsByTicketId(Long tacTicketId) {
        String tacCaseSchemaId = schemaService.getTacCaseSchemaId();
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records?ticket={ticketId}", tacCaseSchemaId, tacTicketId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskCaseResponseRecords<FreshdeskRmaCaseResponseDto> findFreshdeskRmaCaseRecordsByTicketId(Long rmaTicketId) {
        String rmaCaseSchemaId = schemaService.getRMACaseSchemaId();
        return snakeCaseRestClient.get()
                .uri("/custom_objects/schemas/{schema-id}/records?ticket={ticketId}", rmaCaseSchemaId, rmaTicketId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private FreshdeskCaseResponse<FreshdeskTacCaseResponseDto> findFreshdeskTacCaseByDisplayId(String displayId) {
        String tacCaseSchemaId = schemaService.getTacCaseSchemaId();
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
        String rmaCaseSchemaId = schemaService.getRMACaseSchemaId();
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


        String problemDescription = rmaCaseCreateDto.getProblemDescription();
        if (problemDescription != null && escapeHtmlDescription && !escapeHtmlStrings) {
            problemDescription = HtmlUtils.htmlEscape(problemDescription);
        }
        return FreshdeskTicketCreateDto.builder()
                .email(rmaCaseCreateDto.getContactEmail())
                .subject("RMA: "+ tacCaseTicketResponseDto.getSubject())
                .responderId(tacCaseTicketResponseDto.getResponderId())
                .requesterId(tacCaseTicketResponseDto.getRequesterId())
                .companyId(tacCaseTicketResponseDto.getCompanyId())
                .type("Problem")
                .source(Source.Email)
                .status(StatusForTickets.Open)
                .priority(tacCaseTicketResponseDto.getPriorityForTickets())
                .description("RMA for TAC Case: "+rmaCaseCreateDto.getTacCaseId()+":\n"+ problemDescription)
                .parentId(tacCaseTicketResponseDto.getId())
                .tags(List.of("RMA"))
                .build();
    }

    private FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> updateRmaCase(
            String rmaCaseDisplayId, FreshdeskRmaCaseUpdateRequest request) {

        String rmaCaseSchemaId = schemaService.getRMACaseSchemaId();
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
            rmaCaseResponseDto.setProblemDescription(freshdeskTicketResponseDto.getDescriptionText());
            rmaCaseResponseDto.setCaseClosedDate(freshdeskTicketResponseDto.getStats().getClosedAt());
            rmaCaseResponseDto.setCaseCreatedDate(freshdeskTicketResponseDto.getCreatedAt());
        }

        return rmaCaseResponseDto;
    }

    private FreshdeskCaseResponse<FreshdeskRmaCaseResponseDto> createRmaCase(String rmaCaseSchemaId,
                                                                             FreshdeskDataCreateRequest<FreshdeskRmaCaseCreateDto> freshdeskRmaCreateRequest) {
        return snakeCaseRestClient.post()
                .uri("/custom_objects/schemas/{schemaId}/records", rmaCaseSchemaId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(freshdeskRmaCreateRequest)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private static Long extractRmaTicketNumberFromKey(String input) {
        Pattern pattern = Pattern.compile("RMA:(\\d+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null; // Return null if "RMA:" is not found
    }

    private RmaCaseResponseDto mapToRmaCaseResponseDto(
            FreshdeskTicketResponseDto rmaTicket,
            FreshdeskRmaCaseResponseDto rmaCaseData,
            FreshdeskTacCaseResponseDto tacCaseData) {

        return Optional.ofNullable(genericModelMapper.map(rmaCaseData, RmaCaseResponseDto.class))
                .map(rmaCaseResponseDto -> {
                    rmaCaseResponseDto.setId(rmaTicket.getId());
                    rmaCaseResponseDto.setTacCaseId(tacCaseData.getTicket());
                    rmaCaseResponseDto.setCaseStatus(CaseStatus.valueOf(rmaTicket.getStatusForTickets().name()));
                    rmaCaseResponseDto.setProblemDescription(rmaTicket.getDescriptionText());
                    //fixme: look into this sometime
                    //rmaCaseResponseDto.setCaseNumber(rmaCaseData.getTacCase());
                    rmaCaseResponseDto.setCaseClosedDate(rmaTicket.getStats().getClosedAt());
                    rmaCaseResponseDto.setCaseCreatedDate(rmaTicket.getCreatedAt());
                    return rmaCaseResponseDto;
                })
                .orElseThrow(() -> new IllegalStateException("Failed to map RMA case response"));
    }


}
