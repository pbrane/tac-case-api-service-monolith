package com.beaconstrategists.taccaseapiservice.services;

import com.beaconstrategists.taccaseapiservice.controllers.dto.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RmaCaseService {
    // CRUD Operations for RmaCase
    RmaCaseResponseDto save(RmaCaseCreateDto rmaCaseCreateDto);
    RmaCaseResponseDto update(Long id, RmaCaseUpdateDto rmaCaseUpdateDto);
    RmaCaseResponseDto save(RmaCaseResponseDto rmaCaseResponseDto);
    List<RmaCaseResponseDto> findAll();
    Optional<RmaCaseResponseDto> findById(Long id);
    Optional<RmaCaseResponseDto> findByCaseNumber(String caseNumber);
    boolean isExists(Long id);
    boolean isExists(String caseNumber);
    RmaCaseResponseDto partialUpdate(Long id, RmaCaseResponseDto rmaCaseResponseDto);
    RmaCaseResponseDto partialUpdate(String caseNumber, RmaCaseResponseDto rmaCaseResponseDto);
    void delete(Long id);
    void delete(String caseNumber);

    // Attachment Operations
    RmaCaseAttachmentResponseDto addAttachment(Long caseId, RmaCaseAttachmentUploadDto uploadDto) throws IOException;
    List<RmaCaseAttachmentResponseDto> getAllAttachments(Long caseId);
    RmaCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId);
    RmaCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId);
    void deleteAttachment(Long caseId, Long attachmentId);
    void deleteAllAttachments(Long caseId);

    // Note Operations
    RmaCaseNoteResponseDto addNote(Long caseId, RmaCaseNoteUploadDto uploadDto) throws IOException;
    List<RmaCaseNoteResponseDto> getAllNotes(Long caseId);
    RmaCaseNoteDownloadDto getNote(Long caseId, Long noteId);
    void deleteNote(Long caseId, Long noteId);
    void deleteAllNotes(Long caseId);

    List<RmaCaseResponseDto> listRmaCases(OffsetDateTime caseCreateDateFrom,
                                          OffsetDateTime caseCreateDateTo,
                                          OffsetDateTime caseCreateDateSince,
                                          List<CaseStatus> caseStatus,
                                          String logic);

}
