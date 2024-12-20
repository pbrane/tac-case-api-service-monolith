package com.beaconstrategists.taccaseapiservice.model.entities;

import com.beaconstrategists.taccaseapiservice.model.CasePriorityEnum;
import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tac_cases")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //fixme: analyze this
public class TacCaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  private String href;

  private String caseNumber; //fixme: we should probably get rid of this

  @Enumerated(EnumType.STRING)
  private CaseStatus caseStatus;

  private Boolean rmaNeeded;

  @Column(nullable = false, length = 4096)
  private String subject;

  private Integer relatedRmaCount;  //fixme this needs implementation

  private Integer relatedDispatchCount; //fixme this needs implementation

  @Column(nullable = false, length = 65535)
  private String problemDescription;

  @Column(length = 255)
  private String installationCountry;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime firstResponseDate;

  @Column(length = 255)
  private String customerTrackingNumber;

  @Column(nullable = false, length = 255)
  private String contactEmail;

  @Column(length = 255)
  private String productName;

  @Column(length = 255)
  private String productSerialNumber;

  @Column(length = 255)
  private String productFirmwareVersion;

  @Column(length = 255)
  private String productSoftwareVersion;

  @Column(length = 255)
  private String caseSolutionDescription;

  @Enumerated(EnumType.STRING) //fixme: Is this right?
  private CasePriorityEnum casePriority;

  @Column(length = 255)
  private String caseOwner;

  private Integer caseNoteCount; //fixme: get rid of this one

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime caseCreatedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime caseClosedDate;

  @Column(length = 255)
  private String businessImpact;

  @Column(length = 255)
  private String accountNumber;

  @Column(length = 255)
  private String faultySerialNumber;

  @Column(length = 255)
  private String faultyPartNumber;

  @OneToMany(mappedBy = "tacCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<RmaCaseEntity> rmaCases = new ArrayList<>();

  //fixme: this probably should be used in the service/repository layer
  public void addRmaCase(RmaCaseEntity rmaCase) {
    rmaCases.add(rmaCase);
    rmaCase.setTacCase(this);
  }

  public void removeRmaCase(RmaCaseEntity rmaCase) {
    rmaCases.remove(rmaCase);
    rmaCase.setTacCase(null);
  }

  @OneToMany(mappedBy = "tacCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<TacCaseNoteEntity> tacCaseNotes = new ArrayList<>();

  public void addTacCaseNote(TacCaseNoteEntity tacCaseNote) {
    tacCaseNotes.add(tacCaseNote);
    tacCaseNote.setTacCase(this);
  }

  @OneToMany(mappedBy = "tacCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<TacCaseAttachmentEntity> attachments = new ArrayList<>();

  // Helper methods to manage bi-directional relationship
  public void addAttachment(TacCaseAttachmentEntity attachment) {
    attachments.add(attachment);
    attachment.setTacCase(this);
  }

  public void removeAttachment(TacCaseAttachmentEntity attachment) {
    attachments.remove(attachment);
    attachment.setTacCase(null);
  }

}
