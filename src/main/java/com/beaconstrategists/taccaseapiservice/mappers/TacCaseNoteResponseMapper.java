package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseNoteResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;

public interface TacCaseNoteResponseMapper {
    TacCaseNoteResponseDto mapTo(TacCaseNoteEntity entity);
}