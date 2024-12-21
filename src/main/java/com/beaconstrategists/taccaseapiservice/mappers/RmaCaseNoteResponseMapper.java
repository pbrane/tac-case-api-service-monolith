package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseNoteResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseNoteEntity;

public interface RmaCaseNoteResponseMapper {
    RmaCaseNoteResponseDto mapTo(RmaCaseNoteEntity entity);
}