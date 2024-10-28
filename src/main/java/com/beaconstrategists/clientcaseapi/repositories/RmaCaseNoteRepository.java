package com.beaconstrategists.clientcaseapi.repositories;

import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RmaCaseNoteRepository extends JpaRepository<RmaCaseNoteEntity, Long> {
}
