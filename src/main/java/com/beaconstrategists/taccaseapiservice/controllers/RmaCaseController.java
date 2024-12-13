package com.beaconstrategists.taccaseapiservice.controllers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.*;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.services.RmaCaseService;
import jakarta.validation.Valid;
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

import static org.springframework.shell.command.invocation.InvocableShellMethod.log;

@RestController
@RequestMapping("/api/v1/rmaCases")
public class RmaCaseController {

    private final RmaCaseService rmaCaseService;

    public RmaCaseController(RmaCaseService rmaCaseService) {
        this.rmaCaseService = rmaCaseService;
    }

    @GetMapping(path = "")
    public ResponseEntity<List<RmaCaseResponseDto>> listAllRmaCases(
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
        List<RmaCaseResponseDto> rmaCases = rmaCaseService.listRmaCases(
                caseCreateDateFrom,
                caseCreateDateTo,
                caseCreateDateSince,
                caseStatus,
                logic
        );
        return new ResponseEntity<>(rmaCases, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RmaCaseResponseDto> getRmaCase(@PathVariable Long id) {
        Optional<RmaCaseResponseDto> foundRmaCase = rmaCaseService.findById(id);
        return foundRmaCase.map(rmaCaseDto -> new ResponseEntity<>(rmaCaseDto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "")
    public ResponseEntity<RmaCaseResponseDto> createRmaCase(@Valid @RequestBody RmaCaseCreateDto dto) {
        RmaCaseResponseDto rmaCaseResponseDto = rmaCaseService.create(dto);
        return new ResponseEntity<>(rmaCaseResponseDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<RmaCaseResponseDto> updateTacCase(
            @PathVariable Long id,
            @Valid @RequestBody RmaCaseUpdateDto updateDto) {

        //fixme: make sure this isn't redundant
        if (!rmaCaseService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RmaCaseResponseDto rmaCaseSavedDto = rmaCaseService.update(id, updateDto);
        return new ResponseEntity<>(rmaCaseSavedDto, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRmaCase(@PathVariable Long id) {
        rmaCaseService.delete(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Message", "RMA Case ID: " + id + " deleted.");
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }



    //Attachments

    @PostMapping("/{id}/attachments")
    public ResponseEntity<RmaCaseAttachmentResponseDto> uploadAttachment(
            @PathVariable Long id,
            @Valid @ModelAttribute RmaCaseAttachmentUploadDto uploadDto) {

        try {
            //log.debug("uploadDto contents: {}", uploadDto);
            log.debug("Received uploadDto: {}", uploadDto);
            if (uploadDto.getFile() != null) {
                log.debug("Uploaded file: Original Filename={}, Size={}, ContentType={}",
                        uploadDto.getFile().getOriginalFilename(),
                        uploadDto.getFile().getSize(),
                        uploadDto.getFile().getContentType());
            } else {
                log.warn("No file received in the request!");
            }

            RmaCaseAttachmentResponseDto responseDto = rmaCaseService.addAttachment(id, uploadDto);
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
    public ResponseEntity<List<RmaCaseAttachmentResponseDto>> getAllAttachments(@PathVariable Long id) {
        List<RmaCaseAttachmentResponseDto> attachments = rmaCaseService.getAllAttachments(id);
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }

    @GetMapping("/{caseId}/attachments/{attachmentId}")
    public ResponseEntity<RmaCaseAttachmentResponseDto> getAttachment(@PathVariable Long caseId, @PathVariable Long attachmentId) {
        RmaCaseAttachmentResponseDto attachment = rmaCaseService.getAttachment(caseId, attachmentId);
        return new ResponseEntity<>(attachment, HttpStatus.OK);
    }

    @DeleteMapping("/{caseId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long caseId,
            @PathVariable Long attachmentId) {
        rmaCaseService.deleteAttachment(caseId, attachmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/attachments")
    public ResponseEntity<Void> deleteAllAttachments(@PathVariable Long id) {
        rmaCaseService.deleteAllAttachments(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{caseId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long caseId,
            @PathVariable Long attachmentId) {
        RmaCaseAttachmentDownloadDto downloadDto = rmaCaseService.getAttachmentDownload(caseId, attachmentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadDto.getName() + "\"")
                .contentType(MediaType.parseMediaType(downloadDto.getMimeType()))
                .body(downloadDto.getResource());
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<RmaCaseNoteResponseDto> uploadNote(
            @PathVariable Long id,
            @Valid @RequestBody RmaCaseNoteUploadDto uploadDto) {

        try {
            RmaCaseNoteResponseDto responseDto = rmaCaseService.addNote(id, uploadDto);
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
    public ResponseEntity<List<RmaCaseNoteResponseDto>> getAllNotes(@PathVariable Long id) {
        List<RmaCaseNoteResponseDto> notes = rmaCaseService.getAllNotes(id);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/{caseId}/notes/{noteId}")
    public ResponseEntity<RmaCaseNoteDownloadDto> getNote(
            @PathVariable Long caseId, @PathVariable Long noteId) {
        RmaCaseNoteDownloadDto dto = rmaCaseService.getNote(caseId, noteId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{caseId}/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long caseId,
            @PathVariable Long noteId) {
        rmaCaseService.deleteNote(caseId, noteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/notes")
    public ResponseEntity<Void> deleteAllNotes(@PathVariable Long id) {
        rmaCaseService.deleteAllNotes(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{caseId}/notes/{noteId}/download")
    public ResponseEntity<RmaCaseNoteDownloadDto> downloadNote(
            @PathVariable Long caseId,
            @PathVariable Long noteId) {
        RmaCaseNoteDownloadDto downloadDto = rmaCaseService.getNote(caseId, noteId);

        return new ResponseEntity<>(downloadDto, HttpStatus.OK);
    }

}
