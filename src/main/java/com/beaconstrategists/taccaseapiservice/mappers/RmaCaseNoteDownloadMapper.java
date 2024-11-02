package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.RmaCaseNoteDownloadDto;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseNoteEntity;

public interface RmaCaseNoteDownloadMapper {
    RmaCaseNoteDownloadDto mapTo(RmaCaseNoteEntity entity);
}
