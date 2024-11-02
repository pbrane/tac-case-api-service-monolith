package com.beaconstrategists.taccaseapiservice.services.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.*;
import com.beaconstrategists.taccaseapiservice.exceptions.ResourceNotFoundException;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseAttachmentDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseAttachmentResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.impl.RmaCaseMapperImpl;
import com.beaconstrategists.taccaseapiservice.mappers.impl.TacCaseMapperImpl;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;
import com.beaconstrategists.taccaseapiservice.repositories.TacCaseAttachmentRepository;
import com.beaconstrategists.taccaseapiservice.repositories.TacCaseNoteRepository;
import com.beaconstrategists.taccaseapiservice.repositories.TacCaseRepository;
import com.beaconstrategists.taccaseapiservice.services.TacCaseService;
import com.beaconstrategists.taccaseapiservice.specifications.TacCaseSpecification;
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
public class TacCaseServiceImpl implements TacCaseService {

    private final TacCaseRepository tacCaseRepository;
    private final TacCaseAttachmentRepository tacCaseAttachmentRepository;
    private final TacCaseMapperImpl tacCaseMapper;
    private final RmaCaseMapperImpl rmaCaseMapper;
    private final TacCaseAttachmentResponseMapper tacCaseAttachmentResponseMapper;
    private final TacCaseAttachmentDownloadMapper tacCaseAttachmentDownloadMapper;
    private final TacCaseNoteResponseMapper tacCaseNoteResponseMapper;
    private final TacCaseNoteDownloadMapper tacCaseNoteDownloadMapper;
    private final TacCaseNoteRepository tacCaseNoteRepository;


    public TacCaseServiceImpl(TacCaseRepository tacCaseRepository,
                              TacCaseAttachmentRepository tacCaseAttachmentRepository,
                              TacCaseMapperImpl tacCaseMapper, RmaCaseMapperImpl rmaCaseMapper,
                              TacCaseAttachmentResponseMapper tacCaseAttachmentResponseMapper,
                              TacCaseAttachmentDownloadMapper tacCaseAttachmentDownloadMapper, TacCaseNoteResponseMapper tacCaseNoteResponseMapper, TacCaseNoteDownloadMapper tacCaseNoteDownloadMapper, TacCaseNoteRepository tacCaseNoteRepository) {
        this.tacCaseRepository = tacCaseRepository;
        this.tacCaseAttachmentRepository = tacCaseAttachmentRepository;
        this.tacCaseMapper = tacCaseMapper;
        this.rmaCaseMapper = rmaCaseMapper;
        this.tacCaseAttachmentResponseMapper = tacCaseAttachmentResponseMapper;
        this.tacCaseAttachmentDownloadMapper = tacCaseAttachmentDownloadMapper;
        this.tacCaseNoteResponseMapper = tacCaseNoteResponseMapper;
        this.tacCaseNoteDownloadMapper = tacCaseNoteDownloadMapper;
        this.tacCaseNoteRepository = tacCaseNoteRepository;
    }

    // CRUD Operations for TacCase

    @Override
    @Transactional
    public TacCaseDto save(TacCaseDto tacCaseDto) {
        TacCaseEntity tacCaseEntity = tacCaseMapper.mapFrom(tacCaseDto);
        TacCaseEntity savedEntity = tacCaseRepository.save(tacCaseEntity);
        return tacCaseMapper.mapTo(savedEntity);
    }

    @Override
    @Transactional
    public List<TacCaseDto> findAll() {
        List<TacCaseEntity> tacCases = StreamSupport.stream(tacCaseRepository.findAll().spliterator(), false)
                .toList();
        return tacCases.stream()
                .map(tacCaseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<TacCaseDto> findById(Long id) {
        return tacCaseRepository.findById(id)
                .map(tacCaseMapper::mapTo);
    }

    @Override
    @Transactional
    public Optional<TacCaseDto> findByCaseNumber(String caseNumber) {
        return tacCaseRepository.findByCaseNumber(caseNumber)
                .map(tacCaseMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacCaseDto> listTacCases(
            OffsetDateTime caseCreateDateFrom,
            OffsetDateTime caseCreateDateTo,
            OffsetDateTime caseCreateDateSince,
            List<CaseStatus> caseStatus,
            String logic
    ) {
        Specification<TacCaseEntity> specification = TacCaseSpecification.buildSpecification(
                caseCreateDateFrom,
                caseCreateDateTo,
                caseCreateDateSince,
                caseStatus,
                logic
        );

        List<TacCaseEntity> tacCases = tacCaseRepository.findAll(specification);

        return tacCases.stream()
                .map(tacCaseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean exists(Long id) {
        return tacCaseRepository.existsById(id);
    }

    @Override
    @Transactional
    public boolean exists(String caseNumber) {
        return tacCaseRepository.existsByCaseNumber(caseNumber);
    }

/*
    @Override
    @Transactional
    public TacCaseDto partialUpdate(Long id, TacCaseDto tacCaseDto) {
        TacCaseEntity existingTacCase = tacCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case does not exist with id " + id));

        // Map updated fields from DTO to existing entity
        tacCaseMapper.mapFrom(tacCaseDto, existingTacCase);

        TacCaseEntity updatedTacCase = tacCaseRepository.save(existingTacCase);
        return tacCaseMapper.mapTo(updatedTacCase);
    }
*/
@Override
public TacCaseDto partialUpdate(Long id, TacCaseDto tacCaseDto) {
    return tacCaseRepository.findById(id).map(existingTacCase -> {
        Optional.ofNullable(tacCaseDto.getCaseOwner()).ifPresent(existingTacCase::setCaseOwner);
        Optional.ofNullable(tacCaseDto.getCasePriority()).ifPresent(existingTacCase::setCasePriority);
        Optional.ofNullable(tacCaseDto.getCaseStatus()).ifPresent(existingTacCase::setCaseStatus);
        Optional.ofNullable(tacCaseDto.getAccountNumber()).ifPresent(existingTacCase::setAccountNumber);
        Optional.ofNullable(tacCaseDto.getCaseNumber()).ifPresent(existingTacCase::setCaseNumber);
        Optional.ofNullable(tacCaseDto.getAccountNumber()).ifPresent(existingTacCase::setAccountNumber);
        Optional.ofNullable(tacCaseDto.getBusinessImpact()).ifPresent(existingTacCase::setBusinessImpact);
        Optional.ofNullable(tacCaseDto.getCaseClosedDate()).ifPresent(existingTacCase::setCaseClosedDate);
        Optional.ofNullable(tacCaseDto.getCaseCreatedDate()).ifPresent(existingTacCase::setCaseCreatedDate);
        Optional.ofNullable(tacCaseDto.getCaseNoteCount()).ifPresent(existingTacCase::setCaseNoteCount);
        Optional.ofNullable(tacCaseDto.getCaseSolutionDescription()).ifPresent(existingTacCase::setCaseSolutionDescription);
        Optional.ofNullable(tacCaseDto.getContactEmail()).ifPresent(existingTacCase::setContactEmail);
        Optional.ofNullable(tacCaseDto.getCustomerTrackingNumber()).ifPresent(existingTacCase::setCustomerTrackingNumber);
        Optional.ofNullable(tacCaseDto.getFaultyPartNumber()).ifPresent(existingTacCase::setFaultyPartNumber);
        Optional.ofNullable(tacCaseDto.getFaultySerialNumber()).ifPresent(existingTacCase::setFaultySerialNumber);
        Optional.ofNullable(tacCaseDto.getFirstResponseDate()).ifPresent(existingTacCase::setFirstResponseDate);
        Optional.ofNullable(tacCaseDto.getHref()).ifPresent(existingTacCase::setHref); //fixme
        Optional.ofNullable(tacCaseDto.getCustomerTrackingNumber()).ifPresent(existingTacCase::setCustomerTrackingNumber);
        Optional.ofNullable(tacCaseDto.getInstallationCountry()).ifPresent(existingTacCase::setInstallationCountry);
        Optional.ofNullable(tacCaseDto.getProblemDescription()).ifPresent(existingTacCase::setProblemDescription);
        Optional.ofNullable(tacCaseDto.getCaseNoteCount()).ifPresent(existingTacCase::setCaseNoteCount);
        Optional.ofNullable(tacCaseDto.getProductFirmwareVersion()).ifPresent(existingTacCase::setProductFirmwareVersion);
        Optional.ofNullable(tacCaseDto.getProductSerialNumber()).ifPresent(existingTacCase::setProductSerialNumber);
        Optional.ofNullable(tacCaseDto.getProductName()).ifPresent(existingTacCase::setProductName);
        Optional.ofNullable(tacCaseDto.getProductSoftwareVersion()).ifPresent(existingTacCase::setProductSoftwareVersion);
        Optional.ofNullable(tacCaseDto.getRelatedDispatchCount()).ifPresent(existingTacCase::setRelatedDispatchCount);
        Optional.ofNullable(tacCaseDto.getRelatedRmaCount()).ifPresent(existingTacCase::setRelatedRmaCount);
        Optional.ofNullable(tacCaseDto.getRmaNeeded()).ifPresent(existingTacCase::setRmaNeeded);
        Optional.ofNullable(tacCaseDto.getSubject()).ifPresent(existingTacCase::setSubject);

        TacCaseEntity updatedTacCase = tacCaseRepository.save(existingTacCase);
        return tacCaseMapper.mapTo(updatedTacCase);
    }).orElseThrow(() -> new RuntimeException("TAC Case does not exist"));
}

    @Override
    @Transactional
    public TacCaseDto partialUpdate(String caseNumber, TacCaseDto tacCaseDto) {
        TacCaseEntity existingTacCase = tacCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case does not exist with case number " + caseNumber));

        // Map updated fields from DTO to existing entity
        tacCaseMapper.mapFrom(tacCaseDto, existingTacCase);

        TacCaseEntity updatedTacCase = tacCaseRepository.save(existingTacCase);
        return tacCaseMapper.mapTo(updatedTacCase);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TacCaseEntity tacCase = tacCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TacCase not found with id " + id));
        tacCaseRepository.delete(tacCase);
    }

    @Override
    @Transactional
    public void delete(String caseNumber) {
        TacCaseEntity tacCase = tacCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with case number " + caseNumber));
        tacCaseRepository.delete(tacCase);
    }


    // RMAs

    @Override
    public List<RmaCaseDto> listRmaCases(Long id) {
        TacCaseEntity tacCase = tacCaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TacCase not found with id " + id));
        return tacCase.getRmaCases().stream()
                .map(rmaCaseMapper::mapTo)
                .collect(Collectors.toList());
    }


    /* Attachment Ops
        Fixme for this exposes weirdness in the API. Why navigate by caseId if we only require the attachmentId in these operations
    */

    @Override
    @Transactional
    public TacCaseAttachmentResponseDto addAttachment(Long caseId, TacCaseAttachmentUploadDto uploadDto) throws IOException {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        // Extract file and metadata
        MultipartFile file = uploadDto.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must be provided and not empty.");
        }

        // Optional: Validate file type
        validateFileType(file);

        TacCaseAttachmentEntity attachmentEntity = TacCaseAttachmentEntity.builder()
                .name(Optional.ofNullable(uploadDto.getName()).orElse(file.getOriginalFilename()))
                .mimeType(Optional.ofNullable(uploadDto.getMimeType()).orElse(file.getContentType()))
                .content(file.getBytes())
                .description(uploadDto.getDescription())
                .size((float) file.getSize())
                .tacCase(tacCase)
                .build();

        tacCase.addAttachment(attachmentEntity);
        tacCaseAttachmentRepository.save(attachmentEntity);

        return tacCaseAttachmentResponseMapper.mapTo(attachmentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacCaseAttachmentResponseDto> getAllAttachments(Long caseId) {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        return tacCase.getAttachments().stream()
                .map(tacCaseAttachmentResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TacCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId) {
        TacCaseAttachmentEntity attachment = tacCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getTacCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for TAC Case " + caseId));

        return tacCaseAttachmentDownloadMapper.mapTo(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public void getAttachment(Long caseId, Long attachmentId) {
        TacCaseAttachmentEntity attachment = tacCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getTacCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for TAC Case " + caseId));

        tacCaseAttachmentResponseMapper.mapTo(attachment);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long caseId, Long attachmentId) {
        TacCaseAttachmentEntity attachment = tacCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getTacCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for TAC Case " + caseId));

        tacCaseAttachmentRepository.delete(attachment);
    }

    @Override
    @Transactional
    public void deleteAllAttachments(Long caseId) {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        List<TacCaseAttachmentEntity> attachments = tacCase.getAttachments();

        if (!attachments.isEmpty()) {
            tacCaseAttachmentRepository.deleteAll(attachments);
        }
    }


    /* Note Ops
        Fixme for this exposes weirdness in the API. Why navigate by caseId if we only require the attachmentId in these operations
    */

    @Override
    @Transactional
    public TacCaseNoteResponseDto addNote(Long caseId, TacCaseNoteUploadDto uploadDto) {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        TacCaseNoteEntity noteEntity = TacCaseNoteEntity.builder()
                .author(uploadDto.getAuthor())
                .date(uploadDto.getDate())
                .text(uploadDto.getText())
                .tacCase(tacCase)
                .build();

        tacCase.addTacCaseNote(noteEntity);
        tacCaseNoteRepository.save(noteEntity);

        return tacCaseNoteResponseMapper.mapTo(noteEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacCaseNoteResponseDto> getAllNotes(Long caseId) {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        return tacCase.getTacCaseNotes().stream()
                .map(tacCaseNoteResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TacCaseNoteDownloadDto getNote(Long caseId, Long noteId) {
        TacCaseNoteEntity note = tacCaseNoteRepository.findById(noteId)
                .filter(a -> a.getTacCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId + " for TAC Case " + caseId));

        return tacCaseNoteDownloadMapper.mapTo(note);
    }

    @Override
    @Transactional
    public void deleteNote(Long caseId, Long noteId) {
        TacCaseNoteEntity note = tacCaseNoteRepository.findById(noteId)
                .filter(a -> a.getTacCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId + " for TAC Case " + caseId));

        tacCaseNoteRepository.delete(note);
    }

    @Override
    @Transactional
    public void deleteAllNotes(Long caseId) {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        List<TacCaseNoteEntity> notes = tacCase.getTacCaseNotes();
        if (!notes.isEmpty()) {
            tacCaseNoteRepository.deleteAll(notes);
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

}
