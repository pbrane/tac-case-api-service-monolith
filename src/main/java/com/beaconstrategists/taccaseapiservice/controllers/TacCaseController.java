package com.beaconstrategists.taccaseapiservice.controllers;

import com.beaconstrategists.taccaseapiservice.dtos.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.services.TacCaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tacCases")
public class TacCaseController {

    private final TacCaseService tacCaseService;

    public TacCaseController(TacCaseService tacCaseService) {
        this.tacCaseService = tacCaseService;
    }

    @GetMapping(path = "")
    public ResponseEntity<List<TacCaseResponseDto>> listAllTacCases(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime caseCreateDateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime caseCreateDateTo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime caseCreateDateSince,

            @RequestParam(required = false)
            List<CaseStatus> caseStatus,

            @RequestParam(required = false, defaultValue = "AND")
            String logic
    ) {
        List<TacCaseResponseDto> tacCases = tacCaseService.listTacCases(
                caseCreateDateFrom,
                caseCreateDateTo,
                caseCreateDateSince,
                caseStatus,
                logic
        );
        return new ResponseEntity<>(tacCases, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TacCaseResponseDto> getTacCase(@PathVariable Long id) {
        Optional<TacCaseResponseDto> foundTacCase = tacCaseService.findById(id);
        return foundTacCase.map(tacCaseResponseDto -> new ResponseEntity<>(tacCaseResponseDto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "")
    public ResponseEntity<TacCaseResponseDto> createTacCase(@Valid @RequestBody TacCaseCreateDto dto) {
        TacCaseResponseDto tacCaseResponseDtoSaved = tacCaseService.create(dto);
        return new ResponseEntity<>(tacCaseResponseDtoSaved, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TacCaseResponseDto> updateTacCase(
            @PathVariable Long id,
            @Valid @RequestBody TacCaseUpdateDto updateDto) {

        //fixme: make sure this isn't redundant
        if (!tacCaseService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TacCaseResponseDto tacCaseResponseDto = tacCaseService.update(id, updateDto);
        return new ResponseEntity<>(tacCaseResponseDto, HttpStatus.OK);
    }

/*
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTacCase(@PathVariable Long id) {
        tacCaseService.delete(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Message", "TAC Case ID: " + id + " deleted.");
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }
*/

    //RMAs

    @GetMapping("/{id}/rmaCases")
    public ResponseEntity<List<RmaCaseResponseDto>> listAllRmasForTacCase(@PathVariable Long id) {
        List<RmaCaseResponseDto> rmaCases = tacCaseService.listRmaCases(id);
        return new ResponseEntity<>(rmaCases, HttpStatus.OK);
    }


    //Attachments

    @PostMapping("/{id}/attachments")
    public ResponseEntity<TacCaseAttachmentResponseDto> uploadAttachment(
            @PathVariable Long id,
            @Valid @ModelAttribute TacCaseAttachmentUploadDto uploadDto) {

        try {
            TacCaseAttachmentResponseDto responseDto = tacCaseService.addAttachment(id, uploadDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            // Handle file processing exceptions
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<TacCaseAttachmentResponseDto>> getAllAttachments(@PathVariable Long id) {
        List<TacCaseAttachmentResponseDto> attachments = tacCaseService.getAllAttachments(id);
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }

    @GetMapping("/{caseId}/attachments/{attachmentId}")
    public ResponseEntity<TacCaseAttachmentResponseDto> getAttachment(@PathVariable Long caseId, @PathVariable Long attachmentId) {
        TacCaseAttachmentResponseDto attachment = tacCaseService.getAttachment(caseId, attachmentId);
        return new ResponseEntity<>(attachment, HttpStatus.OK);
    }

/*
    @DeleteMapping("/{caseId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long caseId,
            @PathVariable Long attachmentId) {
        tacCaseService.deleteAttachment(caseId, attachmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/attachments")
    public ResponseEntity<Void> deleteAllAttachments(@PathVariable Long id) {
        tacCaseService.deleteAllAttachments(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
*/

    @GetMapping("/{caseId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long caseId,
            @PathVariable Long attachmentId) {
        TacCaseAttachmentDownloadDto downloadDto = tacCaseService.getAttachmentDownload(caseId, attachmentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadDto.getName() + "\"")
                .contentType(MediaType.parseMediaType(downloadDto.getMimeType()))
                .body(downloadDto.getResource());
    }

    // Notes

    @PostMapping("/{id}/notes")
    public ResponseEntity<TacCaseNoteResponseDto> uploadNote(
            @PathVariable Long id,
            @Valid @RequestBody TacCaseNoteUploadDto uploadDto) {

        try {
            TacCaseNoteResponseDto responseDto = tacCaseService.addNote(id, uploadDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            // Handle file processing exceptions
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<TacCaseNoteResponseDto>> getAllNotes(@PathVariable Long id) {
        List<TacCaseNoteResponseDto> notes = tacCaseService.getAllNotes(id);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/{caseId}/notes/{noteId}")
    public ResponseEntity<TacCaseNoteDownloadDto> getNote(
            @PathVariable Long caseId, @PathVariable Long noteId) {
        TacCaseNoteDownloadDto dto = tacCaseService.getNote(caseId, noteId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

/*
    @DeleteMapping("/{caseId}/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long caseId,
            @PathVariable Long noteId) {
        tacCaseService.deleteNote(caseId, noteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/notes")
    public ResponseEntity<Void> deleteAllNotes(@PathVariable Long id) {
        tacCaseService.deleteAllNotes(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
*/

    @GetMapping("/{caseId}/notes/{noteId}/download")
    public ResponseEntity<TacCaseNoteDownloadDto> downloadNote(
            @PathVariable Long caseId,
            @PathVariable Long noteId) {
        TacCaseNoteDownloadDto downloadDto = tacCaseService.getNote(caseId, noteId);

        return new ResponseEntity<>(downloadDto, HttpStatus.OK);
    }

}
