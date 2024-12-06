package com.beaconstrategists.taccaseapiservice.services;

import com.beaconstrategists.taccaseapiservice.controllers.dto.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TacCaseService {
    // CRUD Operations for TacCase
    TacCaseDto save(TacCaseCreateDto tacCaseDto);
    TacCaseDto update(Long id, TacCaseUpdateDto tacCaseUpdateDto);
    TacCaseDto save(TacCaseDto tacCaseDto);
    List<TacCaseDto> findAll();
    Optional<TacCaseDto> findById(Long id);
    Optional<TacCaseDto> findByCaseNumber(String caseNumber);
    boolean exists(Long id);
    boolean exists(String caseNumber);
    TacCaseDto partialUpdate(Long id, TacCaseDto tacCaseDto);
    TacCaseDto partialUpdate(String caseNumber, TacCaseDto tacCaseDto);
    void delete(Long id);
    void delete(String caseNumber);

    // Attachment Operations
    TacCaseAttachmentResponseDto addAttachment(Long caseId, TacCaseAttachmentUploadDto uploadDto) throws IOException;
    List<TacCaseAttachmentResponseDto> getAllAttachments(Long caseId);
    void getAttachment(Long caseId, Long attachmentId);
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
