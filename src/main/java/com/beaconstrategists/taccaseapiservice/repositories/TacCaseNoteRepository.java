package com.beaconstrategists.taccaseapiservice.repositories;

import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TacCaseNoteRepository extends JpaRepository<TacCaseNoteEntity, Long> {
}
