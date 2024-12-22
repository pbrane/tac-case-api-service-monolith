package com.beaconstrategists.taccaseapiservice.services;

import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TacCaseService {
    // CRUD Operations for TacCase

    //fixme: should all these responses be Optionals?
    TacCaseResponseDto create(TacCaseCreateDto tacCaseDto);
    Optional<TacCaseResponseDto> findById(Long id);
    TacCaseResponseDto update(Long id, TacCaseUpdateDto tacCaseUpdateDto);
    void delete(Long id);

    List<TacCaseResponseDto> listTacCases(OffsetDateTime caseCreateDateFrom,
                                          OffsetDateTime caseCreateDateTo,
                                          OffsetDateTime caseCreateDateSince,
                                          List<CaseStatus> caseStatus,
                                          String logic);

    List<TacCaseResponseDto> findAll();

    boolean exists(Long id);

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


    List<RmaCaseResponseDto> listRmaCases(Long id);

}
