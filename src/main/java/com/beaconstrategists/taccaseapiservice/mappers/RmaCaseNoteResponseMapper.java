package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.RmaCaseNoteResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseNoteEntity;

public interface RmaCaseNoteResponseMapper {
    RmaCaseNoteResponseDto mapTo(RmaCaseNoteEntity entity);
}