package com.beaconstrategists.clientcaseapi.mappers;

import com.beaconstrategists.clientcaseapi.controllers.dto.RmaCaseNoteResponseDto;
import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseNoteEntity;

public interface RmaCaseNoteResponseMapper {
    RmaCaseNoteResponseDto mapTo(RmaCaseNoteEntity entity);
}