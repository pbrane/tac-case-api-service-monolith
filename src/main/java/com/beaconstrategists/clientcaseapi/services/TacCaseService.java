package com.beaconstrategists.clientcaseapi.services;

import com.beaconstrategists.clientcaseapi.controllers.dto.*;
import com.beaconstrategists.clientcaseapi.model.CaseStatus;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TacCaseService {
    // CRUD Operations for TacCase
    TacCaseDto save(TacCaseDto tacCaseDto);
    List<TacCaseDto> findAll();
    Optional<TacCaseDto> findById(Long id);
    Optional<TacCaseDto> findByCaseNumber(String caseNumber);
    boolean isExists(Long id);
    boolean isExists(String caseNumber);
    TacCaseDto partialUpdate(Long id, TacCaseDto tacCaseDto);
    TacCaseDto partialUpdate(String caseNumber, TacCaseDto tacCaseDto);
    void delete(Long id);
    void delete(String caseNumber);

    // Attachment Operations
    TacCaseAttachmentResponseDto addAttachment(Long caseId, TacCaseAttachmentUploadDto uploadDto) throws IOException;
    List<TacCaseAttachmentResponseDto> getAllAttachments(Long caseId);
    TacCaseAttachmentResponseDto getAttachment(Long caseId, Long attachmentId);
    TacCaseAttachmentDownloadDto getAttachmentDownload(Long caseId, Long attachmentId);
    void deleteAttachment(Long caseId, Long attachmentId);
    void deleteAllAttachments(Long caseId);

    // Note Operations
    TacCaseNoteResponseDto addNote(Long caseId, TacCaseNoteUploadDto uploadDto) throws IOException;
    List<TacCaseNoteResponseDto> getAllNotes(Long caseId);
    TacCaseNoteDownloadDto getNote(Long caseId, Long noteId);
    void deleteNote(Long caseId, Long noteId);
    void deleteAllNotes(Long caseId);

    List<TacCaseDto> listTacCases(OffsetDateTime caseCreateDateFrom,
                                  OffsetDateTime caseCreateDateTo,
                                  OffsetDateTime caseCreateDateSince,
                                  List<CaseStatus> caseStatus,
                                  String logic);

    List<RmaCaseDto> listRmaCases(Long id);
}
