package com.beaconstrategists.taccaseapiservice.services;

import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RmaCaseService {
    // CRUD Operations for RmaCase

    //fixme: should all these responses be optionals?
    RmaCaseResponseDto create(RmaCaseCreateDto rmaCaseCreateDto);
    Optional<RmaCaseResponseDto> findById(Long id);
    RmaCaseResponseDto update(Long id, RmaCaseUpdateDto rmaCaseUpdateDto);
    void delete(Long id);

    List<RmaCaseResponseDto> listRmaCases(OffsetDateTime caseCreateDateFrom,
                                          OffsetDateTime caseCreateDateTo,
                                          OffsetDateTime caseCreateDateSince,
                                          List<CaseStatus> caseStatus,
                                          String logic,
                                          Integer pageSize,
                                          Integer pageLimit);

    List<RmaCaseResponseDto> findAll();

    boolean exists(Long id);

    // Attachment Operations
    RmaCaseAttachmentResponseDto addAttachment(Long caseId, RmaCaseAttachmentUploadDto uploadDto) throws IOException;
    List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId);
    RmaCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId);
    RmaCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId);
    void deleteAttachment(Long caseId, Long attachmentId);
    void deleteAllAttachments(Long caseId);

    // Note Operations
    RmaCaseNoteResponseDto addNote(Long caseId, RmaCaseNoteUploadDto uploadDto) throws IOException;
    List<RmaCaseNoteResponseDto> getAllNotes(Long caseId, OffsetDateTime sinceDate);
    RmaCaseNoteDownloadDto getNote(Long caseId, Long noteId);
    void deleteNote(Long caseId, Long noteId);
    void deleteAllNotes(Long caseId);

}
