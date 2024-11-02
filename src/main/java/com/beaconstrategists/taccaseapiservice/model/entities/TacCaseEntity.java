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
@Table(
        name = "tac_cases",
        indexes = {@Index(name = "tac_cn_idx", columnList = "caseNumber", unique = true)}
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TacCaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  private String href;

  @Column(nullable = false, unique = true)
  private String caseNumber;

  @Enumerated(EnumType.STRING)
  private CaseStatus caseStatus;

  private Boolean rmaNeeded;

  private String subject;

  private Integer relatedRmaCount;  //fixme this needs implementation

  private Integer relatedDispatchCount; //fixme this needs implementation

  @Lob
  private String problemDescription;

  private String installationCountry;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime firstResponseDate;

  private String customerTrackingNumber;

  @Column(nullable = false)
  private String contactEmail;

  private String productName;

  private String productSerialNumber;

  private String productFirmwareVersion;

  private String productSoftwareVersion;

  private String caseSolutionDescription;

  @Enumerated(EnumType.STRING)
  private CasePriorityEnum casePriority;

  private String caseOwner;

  private Integer caseNoteCount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime caseCreatedDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime caseClosedDate;

  private String businessImpact;

  private String accountNumber;

  private String faultySerialNumber;

  private String faultyPartNumber;

  @OneToMany(mappedBy = "tacCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<RmaCaseEntity> rmaCases = new ArrayList<>();

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
