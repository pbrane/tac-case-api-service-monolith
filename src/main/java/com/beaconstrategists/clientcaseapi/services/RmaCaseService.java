package com.beaconstrategists.clientcaseapi.services;

import com.beaconstrategists.clientcaseapi.controllers.dto.*;
import com.beaconstrategists.clientcaseapi.model.CaseStatus;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RmaCaseService {
    // CRUD Operations for RmaCase
    RmaCaseDto save(RmaCaseDto rmaCaseDto);
    List<RmaCaseDto> findAll();
    Optional<RmaCaseDto> findById(Long id);
    Optional<RmaCaseDto> findByCaseNumber(String caseNumber);
    boolean isExists(Long id);
    boolean isExists(String caseNumber);
    RmaCaseDto partialUpdate(Long id, RmaCaseDto rmaCaseDto);
    RmaCaseDto partialUpdate(String caseNumber, RmaCaseDto rmaCaseDto);
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

    List<RmaCaseDto> listRmaCases(OffsetDateTime caseCreateDateFrom,
                                  OffsetDateTime caseCreateDateTo,
                                  OffsetDateTime caseCreateDateSince,
                                  List<CaseStatus> caseStatus,
                                  String logic);

}
