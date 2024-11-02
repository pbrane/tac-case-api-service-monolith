package com.beaconstrategists.taccaseapiservice.model.entities;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An RMA Case
 */
@Log
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "rma_cases", indexes = {@Index(name = "rma_cn_idx", columnList = "caseNumber", unique = true)})
public class RmaCaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tac_case_id", nullable = false)
  @JsonBackReference
  private TacCaseEntity tacCase;

  private String href;

  private String caseNumber;

  private String requestType;

  private String newPartSerialNumber;

  private String faultySerialNumber;

  private String faultyPartNumber;

  private String returnedSerialNumber;

  private String returnedPartNumber;

  private CaseStatus caseStatus;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime caseCreatedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime caseClosedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime newPartShippedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime newPartDeliveredDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime faultyPartShippedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime faultyPartDeliveredDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime failureAnalysisStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime failureAnalysisInProgressDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime failureAnalysisFinishedDate;

  private String shipToStreet1;

  private String shipToProvince;

  private String shipToPostalCode;

  private String shipToPhone;

  private String shipToCountry;

  private String shipToCity;

  private String shipToContactEmail;

  private String shipToAttention;

  private String shippedDate;

  private String shippedCarrier;

  private String problemDescription;

  private String installationCountry;

  private String customerTrackingNumber;

  private String contactEmail;

  private Integer vendorRmaNumber;

  @OneToMany(mappedBy = "rmaCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<RmaCaseNoteEntity> rmaCaseNotes = new ArrayList<>();

  public void addRmaCaseNote(RmaCaseNoteEntity rmaCaseNote) {
    rmaCaseNotes.add(rmaCaseNote);
    rmaCaseNote.setRmaCase(this);
  }

  @OneToMany(mappedBy = "rmaCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<RmaCaseAttachmentEntity> attachments = new ArrayList<>();

  // Helper methods to manage bi-directional relationship
  public void addAttachment(RmaCaseAttachmentEntity attachment) {
    attachments.add(attachment);
    attachment.setRmaCase(this);
  }

  public void removeAttachment(RmaCaseAttachmentEntity attachment) {
    attachments.remove(attachment);
    attachment.setRmaCase(null);
  }

}