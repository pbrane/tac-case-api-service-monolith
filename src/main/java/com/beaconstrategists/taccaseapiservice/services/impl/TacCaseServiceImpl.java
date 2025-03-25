package com.beaconstrategists.taccaseapiservice.services.impl;

import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.exceptions.ResourceNotFoundException;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseAttachmentDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseAttachmentResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteDownloadMapper;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteResponseMapper;
import com.beaconstrategists.taccaseapiservice.mappers.impl.RmaCaseResponseMapperImpl;
import com.beaconstrategists.taccaseapiservice.mappers.impl.TacCaseCreateMapperImpl;
import com.beaconstrategists.taccaseapiservice.mappers.impl.TacCaseResponseMapperImpl;
import com.beaconstrategists.taccaseapiservice.mappers.impl.TacCaseUpdateMapperImpl;
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

import static org.springframework.shell.command.invocation.InvocableShellMethod.log;

@Service("JpaTacCaseService")
public class TacCaseServiceImpl implements TacCaseService {

    private final TacCaseRepository tacCaseRepository;
    private final TacCaseAttachmentRepository tacCaseAttachmentRepository;
    private final TacCaseResponseMapperImpl tacCaseMapper;
    private final TacCaseCreateMapperImpl tacCaseCreateMapper;
    private final TacCaseUpdateMapperImpl tacCaseUpdateMapper;
    private final TacCaseAttachmentResponseMapper tacCaseAttachmentResponseMapper;
    private final TacCaseAttachmentDownloadMapper tacCaseAttachmentDownloadMapper;
    private final TacCaseNoteResponseMapper tacCaseNoteResponseMapper;
    private final TacCaseNoteDownloadMapper tacCaseNoteDownloadMapper;
    private final TacCaseNoteRepository tacCaseNoteRepository;

    private final RmaCaseResponseMapperImpl rmaCaseResponseMapper;

    public TacCaseServiceImpl(TacCaseRepository tacCaseRepository,
                              TacCaseAttachmentRepository tacCaseAttachmentRepository,
                              TacCaseResponseMapperImpl tacCaseMapper,
                              TacCaseCreateMapperImpl tacCaseCreateMapper,
                              TacCaseUpdateMapperImpl tacCaseUpdateMapper,
                              RmaCaseResponseMapperImpl rmaCaseResponseMapper,
                              TacCaseAttachmentResponseMapper tacCaseAttachmentResponseMapper,
                              TacCaseAttachmentDownloadMapper tacCaseAttachmentDownloadMapper,
                              TacCaseNoteResponseMapper tacCaseNoteResponseMapper,
                              TacCaseNoteDownloadMapper tacCaseNoteDownloadMapper,
                              TacCaseNoteRepository tacCaseNoteRepository) {

        this.tacCaseRepository = tacCaseRepository;
        this.tacCaseAttachmentRepository = tacCaseAttachmentRepository;
        this.tacCaseMapper = tacCaseMapper;
        this.tacCaseCreateMapper = tacCaseCreateMapper;
        this.tacCaseUpdateMapper = tacCaseUpdateMapper;
        this.tacCaseAttachmentResponseMapper = tacCaseAttachmentResponseMapper;
        this.tacCaseAttachmentDownloadMapper = tacCaseAttachmentDownloadMapper;
        this.tacCaseNoteResponseMapper = tacCaseNoteResponseMapper;
        this.tacCaseNoteDownloadMapper = tacCaseNoteDownloadMapper;
        this.tacCaseNoteRepository = tacCaseNoteRepository;

        this.rmaCaseResponseMapper = rmaCaseResponseMapper;
    }

    // CRUD Operations for TacCase

    @Override
    @Transactional
    public TacCaseResponseDto create(TacCaseCreateDto tacCaseDto) {
        TacCaseEntity tacCaseEntity = tacCaseCreateMapper.mapFrom(tacCaseDto);
        tacCaseEntity.setCaseCreatedDate(OffsetDateTime.now());
        TacCaseEntity savedEntity = tacCaseRepository.save(tacCaseEntity);
        return tacCaseMapper.mapTo(savedEntity);
    }

    @Override
    @Transactional
    public TacCaseResponseDto update(Long id, TacCaseUpdateDto updateDto) {

        /*
        This appears redundant because the controller checks before
        making this call. However, the case could be deleted between
        then the save. This is all wrapped in a transaction to handle
        that situation gracefully.
        */
        TacCaseEntity tacCaseEntity = tacCaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No TAC Case found with ID: " + id));
        tacCaseUpdateMapper.map(updateDto, tacCaseEntity);
        TacCaseEntity savedEntity = tacCaseRepository.save(tacCaseEntity);
        return tacCaseMapper.mapTo(savedEntity);
    }

    @Override
    @Transactional
    public List<TacCaseResponseDto> findAll() {
        List<TacCaseEntity> tacCases = StreamSupport.stream(tacCaseRepository.findAll().spliterator(), false)
                .toList();
        return tacCases.stream()
                .map(tacCaseMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<TacCaseResponseDto> findById(Long id) {
        return tacCaseRepository.findById(id)
                .map(tacCaseMapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacCaseResponseDto> listTacCases(
            OffsetDateTime caseCreateDateFrom,
            OffsetDateTime caseCreateDateTo,
            OffsetDateTime caseCreateDateSince,
            List<CaseStatus> caseStatus,
            String logic,
            Integer pageSize,
            Integer pageLimit) {

        Specification<TacCaseEntity> specification = TacCaseSpecification.buildSpecification(
                caseCreateDateFrom,
                caseCreateDateTo,
                caseCreateDateSince,
                caseStatus,
                logic
        );

        List<TacCaseEntity> tacCases = tacCaseRepository.findAll(specification);

        return tacCases.stream()
                .limit((long) pageSize * pageLimit)
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
    public void delete(Long id) {
        TacCaseEntity tacCase = tacCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TacCase not found with id " + id));
        tacCaseRepository.delete(tacCase);
    }

    // RMAs

    @Override
    public List<RmaCaseResponseDto> listRmaCases(Long id) {
        TacCaseEntity tacCase = tacCaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TacCase not found with id " + id));
        return tacCase.getRmaCases().stream()
                .map(rmaCaseResponseMapper::mapTo)
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

        //fixme: need better response body when invalid type is sent
        try {
            validateFileType(file);
        } catch (IllegalArgumentException e) {
            log.error("Unsupported file type: {}", file.getContentType(), e);
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
        }

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
    public TacCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId) {
        TacCaseAttachmentEntity attachment = tacCaseAttachmentRepository.findById(attachmentId)
                .filter(a -> a.getTacCase().getId().equals(caseId))
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id " + attachmentId + " for TAC Case " + caseId));

        return tacCaseAttachmentResponseMapper.mapTo(attachment);
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
    public List<TacCaseNoteResponseDto> getAllNotes(Long caseId, OffsetDateTime sinceDate) {
        TacCaseEntity tacCase = tacCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TAC Case not found with id " + caseId));

        return tacCase.getTacCaseNotes().stream()
                .filter(rec -> sinceDate == null || !rec.getDate().isBefore(sinceDate))
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
}
