package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseNoteResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;

public interface TacCaseNoteResponseMapper {
    TacCaseNoteResponseDto mapTo(TacCaseNoteEntity entity);
}