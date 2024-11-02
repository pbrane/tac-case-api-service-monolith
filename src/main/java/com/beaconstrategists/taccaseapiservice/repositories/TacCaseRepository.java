package com.beaconstrategists.taccaseapiservice.repositories;

import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TacCaseRepository extends JpaRepository<TacCaseEntity, Long>, JpaSpecificationExecutor<TacCaseEntity> {

    Optional<TacCaseEntity> findByCaseNumber(String caseNumber);
    boolean existsByCaseNumber(String caseNumber);
    void deleteByCaseNumber(String caseNumber);
}
