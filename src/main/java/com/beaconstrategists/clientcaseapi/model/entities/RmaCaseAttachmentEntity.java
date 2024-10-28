package com.beaconstrategists.clientcaseapi.model.entities;

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
        name = "rma_case_attachments",
        indexes = {@Index(name = "idx_rma_case_id", columnList = "rma_case_id")} //fixme
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RmaCaseAttachmentEntity {

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
    @JoinColumn(name = "rma_case_id", nullable = false)
    @JsonBackReference
    private RmaCaseEntity rmaCase;

}
