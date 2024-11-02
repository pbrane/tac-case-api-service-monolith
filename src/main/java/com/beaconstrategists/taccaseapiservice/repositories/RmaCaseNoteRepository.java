package com.beaconstrategists.taccaseapiservice.repositories;

import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RmaCaseNoteRepository extends JpaRepository<RmaCaseNoteEntity, Long> {
}
