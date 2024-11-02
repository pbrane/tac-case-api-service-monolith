package com.beaconstrategists.taccaseapiservice.repositories;

import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TacCaseAttachmentRepository extends JpaRepository<TacCaseAttachmentEntity, Long> {
    Optional<TacCaseAttachmentEntity> findByIdAndTacCaseId(Long id, Long tacCaseId);
    List<TacCaseAttachmentEntity> findAllByTacCaseId(Long tacCaseId);
}
