package com.beaconstrategists.taccaseapiservice.services.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.*;
import com.beaconstrategists.taccaseapiservice.exceptions.ResourceNotFoundException;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseNoteDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseNoteResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.impl.RmaCaseMapperImpl;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.entities.*;
import com.beaconstrategists.taccaseapiservice.repositories.RmaCaseAttachmentRepository;
import com.beaconstrategists.taccaseapiservice.repositories.RmaCaseNoteRepository;
import com.beaconstrategists.taccaseapiservice.repositories.RmaCaseRepository;
import com.beaconstrategists.taccaseapiservice.services.RmaCaseService;
import com.beaconstrategists.taccaseapiservice.specifications.RmaCaseSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RmaCaseServiceImpl implements RmaCaseService {

    private final RmaCaseRepository rmaCaseRepository;
    private final RmaCaseAttachmentRepository rmaCaseAttachmentRepository;
    private final RmaCaseNoteRepository rmaCaseNoteRepository;
    private final RmaCaseMapperImpl rmaCaseMapper;
    private final RmaCaseAttachmentResponseMapper attachmentResponseMapper;
    private final RmaCaseAttachmentDownloadMapper attachmentDownloadMapper;
    private final RmaCaseNoteDownloadMapper noteDownloadMapper;
    private final RmaCaseNoteResponseMapper noteResponseMapper;

    public RmaCaseServiceImpl(RmaCaseRepository rmaCaseRepository,
                              RmaCaseAttachmentRepository rmaCaseAttachmentRepository,
                              RmaCaseNoteRepository rmaCaseNoteRepository,
                              RmaCaseMapperImpl rmaCaseMapper,
                              RmaCaseAttachmentResponseMapper attachmentResponseMapper,
                              RmaCaseAttachmentDownloadMapper attachmentDownloadMapper,
                              RmaCaseNoteDownloadMapper noteDownloadMapper,
                              RmaCaseNoteResponseMapper noteResponseMapper) {
        this.rmaCaseRepository = rmaCaseRepository;
        this.rmaCaseAttachmentRepository = rmaCaseAttachmentRepository;
        this.rmaCaseNoteRepository = rmaCaseNoteRepository;
        this.rmaCaseMapper = rmaCaseMapper;
        this.attachmentResponseMapper = attachmentResponseMapper;
        this.attachmentDownloadMapper = attachmentDownloadMapper;
        this.noteDownloadMapper = noteDownloadMapper;
        this.noteResponseMapper = noteResponseMapper;
    }

    // CRUD Operations for RmaCase

    @Override
    @Transactional
    public RmaCaseDto save(RmaCaseDto rmaCaseDto) {
        RmaCaseEntity rmaCaseEntity = rmaCaseMapper.mapFrom(rmaCaseDto);
        RmaCaseEntity savedEntity = rmaCaseRepository.save(rmaCaseEntity);
        return rmaCaseMapper.mapTo(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseDto> findAll() {
        List<RmaCaseEntity> rmaCases = StreamSupport.stream(rmaCaseRepository.findAll().spliterator(), false)
                .toList();
        return rmaCases.stream()
                .map(rmaCaseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RmaCaseDto> findById(Long id) {
        return rmaCaseRepository.findById(id)
                .map(rmaCaseMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RmaCaseDto> findByCaseNumber(String caseNumber) {
        return rmaCaseRepository.findByCaseNumber(caseNumber)
                .map(rmaCaseMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseDto> listRmaCases(
            OffsetDateTime caseCreateDateFrom,
            OffsetDateTime caseCreateDateTo,
            OffsetDateTime caseCreateDateSince,
            List<CaseStatus> caseStatus,
            String logic
    ) {
        Specification<RmaCaseEntity> specification = RmaCaseSpecification.buildSpecification(
                caseCreateDateFrom,
                caseCreateDateTo,
                caseCreateDateSince,
                caseStatus,
                logic
        );

        List<RmaCaseEntity> rmaCases = rmaCaseRepository.findAll(specification);

        return rmaCases.stream()
                .map(rmaCaseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(Long id) {
        return rmaCaseRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(String caseNumber) {
        return rmaCaseRepository.existsByCaseNumber(caseNumber);
    }

/*
    @Override
    @Transactional
    public RmaCaseDto partialUpdate(Long id, RmaCaseDto rmaCaseDto) {
        RmaCaseEntity existingRmaCase = rmaCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case does not exist with id " + id));

        // Map updated fields from DTO to existing entity
        rmaCaseMapper.mapFrom(rmaCaseDto, existingRmaCase);

        RmaCaseEntity updatedRmaCase = rmaCaseRepository.save(existingRmaCase);
        return rmaCaseMapper.mapTo(updatedRmaCase);
    }
*/

    @Override
    public RmaCaseDto partialUpdate(Long id, RmaCaseDto rmaCaseDto) {
        return rmaCaseRepository.findById(id).map(existingRmaCase -> {
            Optional.ofNullable(rmaCaseDto.getCaseNumber()).ifPresent(existingRmaCase::setCaseNumber);
            Optional.ofNullable(rmaCaseDto.getCaseStatus()).ifPresent(existingRmaCase::setCaseStatus);
            Optional.ofNullable(rmaCaseDto.getCustomerTrackingNumber()).ifPresent(existingRmaCase::setCustomerTrackingNumber);
            Optional.ofNullable(rmaCaseDto.getContactEmail()).ifPresent(existingRmaCase::setContactEmail);
            Optional.ofNullable(rmaCaseDto.getCustomerTrackingNumber()).ifPresent(existingRmaCase::setCustomerTrackingNumber);
            Optional.ofNullable(rmaCaseDto.getFailureAnalysisFinishedDate()).ifPresent(existingRmaCase::setFailureAnalysisFinishedDate);
            Optional.ofNullable(rmaCaseDto.getFailureAnalysisInProgressDate()).ifPresent(existingRmaCase::setFailureAnalysisInProgressDate);
            Optional.ofNullable(rmaCaseDto.getFailureAnalysisStartDate()).ifPresent(existingRmaCase::setFailureAnalysisStartDate);
            Optional.ofNullable(rmaCaseDto.getFaultyPartDeliveredDate()).ifPresent(existingRmaCase::setFaultyPartDeliveredDate);
            Optional.ofNullable(rmaCaseDto.getFaultyPartNumber()).ifPresent(existingRmaCase::setFaultyPartNumber);
            Optional.ofNullable(rmaCaseDto.getFaultyPartShippedDate()).ifPresent(existingRmaCase::setFaultyPartShippedDate);
            Optional.ofNullable(rmaCaseDto.getFaultySerialNumber()).ifPresent(existingRmaCase::setFaultySerialNumber);
            Optional.ofNullable(rmaCaseDto.getHref()).ifPresent(existingRmaCase::setHref);
            Optional.ofNullable(rmaCaseDto.getInstallationCountry()).ifPresent(existingRmaCase::setInstallationCountry);
            Optional.ofNullable(rmaCaseDto.getNewPartDeliveredDate()).ifPresent(existingRmaCase::setNewPartDeliveredDate);
            Optional.ofNullable(rmaCaseDto.getNewPartSerialNumber()).ifPresent(existingRmaCase::setNewPartSerialNumber);
            Optional.ofNullable(rmaCaseDto.getNewPartShippedDate()).ifPresent(existingRmaCase::setNewPartShippedDate);
            Optional.ofNullable(rmaCaseDto.getProblemDescription()).ifPresent(existingRmaCase::setProblemDescription);
            Optional.ofNullable(rmaCaseDto.getRequestType()).ifPresent(existingRmaCase::setRequestType);
            Optional.ofNullable(rmaCaseDto.getReturnedPartNumber()).ifPresent(existingRmaCase::setReturnedPartNumber);
            Optional.ofNullable(rmaCaseDto.getReturnedSerialNumber()).ifPresent(existingRmaCase::setReturnedSerialNumber);
            Optional.ofNullable(rmaCaseDto.getShippedCarrier()).ifPresent(existingRmaCase::setShippedCarrier);
            Optional.ofNullable(rmaCaseDto.getShippedDate()).ifPresent(existingRmaCase::setShippedDate);
            Optional.ofNullable(rmaCaseDto.getShipToAttention()).ifPresent(existingRmaCase::setShipToAttention);
            Optional.ofNullable(rmaCaseDto.getShipToCity()).ifPresent(existingRmaCase::setShipToCity);
            Optional.ofNullable(rmaCaseDto.getShipToContactEmail()).ifPresent(existingRmaCase::setShipToContactEmail);
            Optional.ofNullable(rmaCaseDto.getShipToCountry()).ifPresent(existingRmaCase::setShipToCountry);
            Optional.ofNullable(rmaCaseDto.getShipToPhone()).ifPresent(existingRmaCase::setShipToPhone);
            Optional.ofNullable(rmaCaseDto.getShipToPostalCode()).ifPresent(existingRmaCase::setShipToPostalCode);
            Optional.ofNullable(rmaCaseDto.getShipToProvince()).ifPresent(existingRmaCase::setShipToProvince);
            Optional.ofNullable(rmaCaseDto.getShipToStreet1()).ifPresent(existingRmaCase::setShipToStreet1);
//            Optional.ofNullable(rmaCaseDto.getTacCaseId()).ifPresent(existingRmaCase::setTacCase);
            Optional.ofNullable(rmaCaseDto.getVendorRmaNumber()).ifPresent(existingRmaCase::setVendorRmaNumber);

            RmaCaseEntity updatedRmaCase = rmaCaseRepository.save(existingRmaCase);
            return rmaCaseMapper.mapTo(updatedRmaCase);
        }).orElseThrow(() -> new RuntimeException("RMA Case does not exist"));
    }

    @Override
    @Transactional
    public RmaCaseDto partialUpdate(String caseNumber, RmaCaseDto rmaCaseDto) {
        RmaCaseEntity existingRmaCase = rmaCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case does not exist with case number " + caseNumber));

        // Map updated fields from DTO to existing entity
        rmaCaseMapper.mapFrom(rmaCaseDto, existingRmaCase);

        RmaCaseEntity updatedRmaCase = rmaCaseRepository.save(existingRmaCase);
        return rmaCaseMapper.mapTo(updatedRmaCase);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RmaCase not found with id " + id));
        rmaCaseRepository.delete(rmaCase);
    }

    @Override
    @Transactional
    public void delete(String caseNumber) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with case number " + caseNumber));
        rmaCaseRepository.delete(rmaCase);
    }




    // Attachment Operations

    @Override
    @Transactional
    public RmaCaseAttachmentResponseDto addAttachment(Long caseId, RmaCaseAttachmentUploadDto uploadDto) throws IOException {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        // Extract file and metadata
        MultipartFile file = uploadDto.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must be provided and not empty.");
        }

        // Optional: Validate file type
        validateFileType(file);

        RmaCaseAttachmentEntity attachmentEntity = RmaCaseAttachmentEntity.builder()
                .name(Optional.ofNullable(uploadDto.getName()).orElse(file.getOriginalFilename()))
                .mimeType(Optional.ofNullable(uploadDto.getMimeType()).orElse(file.getContentType()))
                .content(file.getBytes())
                .description(uploadDto.getDescription())
                .size((float) file.getSize())
                .rmaCase(rmaCase)
                .build();

        rmaCase.addAttachment(attachmentEntity);
        rmaCaseAttachmentRepository.save(attachmentEntity);

        return attachmentResponseMapper.mapTo(attachmentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        return rmaCase.getAttachments().stream()
                .map(attachmentResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RmaCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId) {
        RmaCaseAttachmentEntity attachment = rmaCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for RMA Case " + caseId));

        return attachmentResponseMapper.mapTo(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public RmaCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId) {
        RmaCaseAttachmentEntity attachment = rmaCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for RMA Case " + caseId));

        return attachmentDownloadMapper.mapTo(attachment);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long caseId, Long attachmentId) {
        RmaCaseAttachmentEntity attachment = rmaCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for RMA Case " + caseId));

        rmaCaseAttachmentRepository.delete(attachment);
    }

    @Override
    @Transactional
    public void deleteAllAttachments(Long caseId) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        List<RmaCaseAttachmentEntity> attachments = rmaCase.getAttachments();

        if (!attachments.isEmpty()) {
            rmaCaseAttachmentRepository.deleteAll(attachments);
        }
    }

        /* Note Ops
        Fixme for this exposes weirdness in the API. Why navigate by caseId if we only require the attachmentId in these operations
    */

    @Override
    @Transactional
    public RmaCaseNoteResponseDto addNote(Long caseId, RmaCaseNoteUploadDto uploadDto) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        RmaCaseNoteEntity noteEntity = RmaCaseNoteEntity.builder()
                .author(uploadDto.getAuthor())
                .date(uploadDto.getDate())
                .text(uploadDto.getText())
                .rmaCase(rmaCase)
                .build();

        rmaCase.addRmaCaseNote(noteEntity);
        rmaCaseNoteRepository.save(noteEntity);

        return noteResponseMapper.mapTo(noteEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseNoteResponseDto> getAllNotes(Long caseId) {
        RmaCaseEntity tacCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        return tacCase.getRmaCaseNotes().stream()
                .map(noteResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RmaCaseNoteDownloadDto getNote(Long caseId, Long noteId) {
        RmaCaseNoteEntity note = rmaCaseNoteRepository.findById(noteId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId + " for RMA Case " + caseId));

        return noteDownloadMapper.mapTo(note);
    }

    @Override
    @Transactional
    public void deleteNote(Long caseId, Long noteId) {
        RmaCaseNoteEntity note = rmaCaseNoteRepository.findById(noteId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId + " for RMA Case " + caseId));

        rmaCaseNoteRepository.delete(note);
    }

    @Override
    @Transactional
    public void deleteAllNotes(Long caseId) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        List<RmaCaseNoteEntity> notes = rmaCase.getRmaCaseNotes();
        if (!notes.isEmpty()) {
            rmaCaseNoteRepository.deleteAll(notes);
        }
    }



    /**
     * Validates the MIME type of the uploaded file.
     *
     * @param file the uploaded MultipartFile
     */
    private void validateFileType(MultipartFile file) {
        List<String> allowedMimeTypes = Arrays.asList("application/pdf", "image/jpeg", "image/png"); // Extend as needed
        if (!allowedMimeTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
        }
    }

    /*
    @Override
    @Transactional
    public RmaCaseAttachmentDetailDto updateAttachment(Long caseId, Long attachmentId, RmaCaseAttachmentDetailDto rmaCaseAttachmentDetailDto) {
        RmaCaseAttachmentEntity existingAttachment = rmaCaseAttachmentRepository.findByIdAndRmaCaseId(attachmentId, caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for RmaCase " + caseId));

        // Map updated fields from DTO to existing entity
        attachmentDetailMapper.mapFrom(rmaCaseAttachmentDetailDto, existingAttachment);

        RmaCaseAttachmentEntity updatedAttachment = rmaCaseAttachmentRepository.save(existingAttachment);
        return attachmentDetailMapper.mapTo(updatedAttachment);
    }
    */

}
