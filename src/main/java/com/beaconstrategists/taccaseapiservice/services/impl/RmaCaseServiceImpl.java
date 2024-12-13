package com.beaconstrategists.taccaseapiservice.services.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.*;
import com.beaconstrategists.taccaseapiservice.exceptions.ResourceNotFoundException;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseNoteDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseNoteResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.impl.RmaCaseCreateMapperImpl;
import com.beaconstrategists.taccaseapiservice.mappers.impl.RmaCaseResponseMapperImpl;
import com.beaconstrategists.taccaseapiservice.mappers.impl.RmaCaseUpdateMapperImpl;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseNoteEntity;
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

import static org.springframework.shell.command.invocation.InvocableShellMethod.log;

@Service
public class RmaCaseServiceImpl implements RmaCaseService {


    private final RmaCaseRepository rmaCaseRepository;
    private final RmaCaseAttachmentRepository rmaCaseAttachmentRepository;
    private final RmaCaseResponseMapperImpl rmaCaseMapper;
    private final RmaCaseCreateMapperImpl rmaCaseCreateMapper;
    private final RmaCaseUpdateMapperImpl rmaCaseUpdateMapper;
    private final RmaCaseAttachmentResponseMapper rmaCaseAttachmentResponseMapper;
    private final RmaCaseAttachmentDownloadMapper rmaCaseAttachmentDownloadMapper;
    private final RmaCaseNoteResponseMapper rmaCaseNoteResponseMapper;
    private final RmaCaseNoteDownloadMapper rmaCaseNoteDownloadMapper;
    private final RmaCaseNoteRepository rmaCaseNoteRepository;



    public RmaCaseServiceImpl(RmaCaseRepository rmaCaseRepository,
                              RmaCaseAttachmentRepository rmaCaseAttachmentRepository,
                              RmaCaseNoteRepository rmaCaseNoteRepository,
                              RmaCaseResponseMapperImpl rmaCaseMapper,
                              RmaCaseCreateMapperImpl rmaCaseCreateMapper, RmaCaseUpdateMapperImpl rmaCaseUpdateMapper,
                              RmaCaseAttachmentResponseMapper rmaCaseAttachmentResponseMapper,
                              RmaCaseAttachmentDownloadMapper rmaCaseAttachmentDownloadMapper,
                              RmaCaseNoteDownloadMapper rmaCaseNoteDownloadMapper,
                              RmaCaseNoteResponseMapper rmaCaseNoteResponseMapper) {



        this.rmaCaseRepository = rmaCaseRepository;
        this.rmaCaseAttachmentRepository = rmaCaseAttachmentRepository;
        this.rmaCaseMapper = rmaCaseMapper;
        this.rmaCaseCreateMapper = rmaCaseCreateMapper;
        this.rmaCaseUpdateMapper = rmaCaseUpdateMapper;
        this.rmaCaseAttachmentResponseMapper = rmaCaseAttachmentResponseMapper;
        this.rmaCaseAttachmentDownloadMapper = rmaCaseAttachmentDownloadMapper;
        this.rmaCaseNoteResponseMapper = rmaCaseNoteResponseMapper;
        this.rmaCaseNoteDownloadMapper = rmaCaseNoteDownloadMapper;
        this.rmaCaseNoteRepository = rmaCaseNoteRepository;
    }



    // CRUD Operations for RmaCase

    @Override
    @Transactional
    public RmaCaseResponseDto create(RmaCaseCreateDto rmaCaseCreateDto) {
        RmaCaseEntity rmaCaseEntity = rmaCaseCreateMapper.mapFrom(rmaCaseCreateDto);
        RmaCaseEntity savedEntity = rmaCaseRepository.save(rmaCaseEntity);
        return rmaCaseMapper.mapTo(savedEntity);
    }

    @Override
    @Transactional
    public RmaCaseResponseDto update(Long id, RmaCaseUpdateDto updateDto) {

        /*
        This appears redundant because the controller checks before
        making this call. However, the case could be deleted between
        then the save. This is all wrapped in a transaction to handle
        that situation gracefully.
        */
        RmaCaseEntity rmaCaseEntity = rmaCaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No RMA Case found with ID: " + id));
        rmaCaseUpdateMapper.map(updateDto, rmaCaseEntity);
        RmaCaseEntity savedEntity = rmaCaseRepository.save(rmaCaseEntity);
        return rmaCaseMapper.mapTo(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseResponseDto> findAll() {
        List<RmaCaseEntity> rmaCases = StreamSupport.stream(rmaCaseRepository.findAll().spliterator(), false)
                .toList();
        return rmaCases.stream()
                .map(rmaCaseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RmaCaseResponseDto> findById(Long id) {
        return rmaCaseRepository.findById(id)
                .map(rmaCaseMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseResponseDto> listRmaCases(
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
    public boolean exists(Long id) {
        return rmaCaseRepository.existsById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RmaCase not found with id " + id));
        rmaCaseRepository.delete(rmaCase);
    }


    /* Attachment Ops
        Fixme for this exposes weirdness in the API. Why navigate by caseId if we only require the attachmentId in these operations
    */

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

        //fixme: need better response body when invalid type is sent
        try {
            validateFileType(file);
        } catch (IllegalArgumentException e) {
            log.error("Unsupported file type: {}", file.getContentType(), e);
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
        }

        RmaCaseAttachmentEntity attachmentEntity = RmaCaseAttachmentEntity.builder()
                .name(Optional.ofNullable(uploadDto.getName()).orElse(file.getOriginalFilename()))
                .mimeType(Optional.ofNullable(uploadDto.getMimeType()).orElse(file.getContentType()))
                .content(file.getBytes())
                .description(uploadDto.getDescription())
                .size((float) file.getSize())
                .rmaCase(rmaCase)
                .build();
        log.debug("Constructed Attachment Entity: {}", attachmentEntity);

        rmaCase.addAttachment(attachmentEntity);
        rmaCaseAttachmentRepository.save(attachmentEntity);
        log.debug("Attachment {} saved successfully for RMA Case ID: {}", attachmentEntity.getId(), caseId);

        return rmaCaseAttachmentResponseMapper.mapTo(attachmentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId) {
        RmaCaseEntity rmaCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        return rmaCase.getAttachments().stream()
                .map(rmaCaseAttachmentResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RmaCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId) {
        RmaCaseAttachmentEntity attachment = rmaCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for RMA Case " + caseId));

        return rmaCaseAttachmentResponseMapper.mapTo(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public RmaCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId) {
        RmaCaseAttachmentEntity attachment = rmaCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for RMA Case " + caseId));

        return rmaCaseAttachmentDownloadMapper.mapTo(attachment);
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

        return rmaCaseNoteResponseMapper.mapTo(noteEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RmaCaseNoteResponseDto> getAllNotes(Long caseId) {
        RmaCaseEntity tacCase = rmaCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("RMA Case not found with id " + caseId));

        return tacCase.getRmaCaseNotes().stream()
                .map(rmaCaseNoteResponseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RmaCaseNoteDownloadDto getNote(Long caseId, Long noteId) {
        RmaCaseNoteEntity note = rmaCaseNoteRepository.findById(noteId)
                .filter(a -> a.getRmaCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + noteId + " for RMA Case " + caseId));

        return rmaCaseNoteDownloadMapper.mapTo(note);
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



    // fixme: Update this list with input from customer
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

        long maxFileSize = 20 * 1024 * 1024;

        // Validate file type
        if (!allowedMimeTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType()
                    + ". Allowed types: " + String.join(", ", allowedMimeTypes));
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 20MB. " +
                    "Uploaded file size: " + (file.getSize() / (1024 * 1024)) + "MB");
        }

    }
}
