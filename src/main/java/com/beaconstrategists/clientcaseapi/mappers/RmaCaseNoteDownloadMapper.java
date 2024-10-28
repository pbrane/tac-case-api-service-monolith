package com.beaconstrategists.clientcaseapi.mappers;

import com.beaconstrategists.clientcaseapi.controllers.dto.RmaCaseNoteDownloadDto;
import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseNoteEntity;

public interface RmaCaseNoteDownloadMapper {
    RmaCaseNoteDownloadDto mapTo(RmaCaseNoteEntity entity);
}
