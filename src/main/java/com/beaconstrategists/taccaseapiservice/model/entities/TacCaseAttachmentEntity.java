package com.beaconstrategists.taccaseapiservice.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tac_case_attachments",
        indexes = {@Index(name = "idx_tac_case_id", columnList = "tac_case_id")}
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TacCaseAttachmentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(nullable = false)
  private String name;

  private String mimeType;

  @Lob
  @Column(nullable = false)
  private byte[] content;

  private String description;

  private Float size;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tac_case_id", nullable = false)
  @JsonBackReference
  private TacCaseEntity tacCase;
}
