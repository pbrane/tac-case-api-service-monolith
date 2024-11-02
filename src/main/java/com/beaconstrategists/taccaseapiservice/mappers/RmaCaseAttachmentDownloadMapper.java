package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.RmaCaseAttachmentDownloadDto;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;

public interface RmaCaseAttachmentDownloadMapper {
    RmaCaseAttachmentDownloadDto mapTo(RmaCaseAttachmentEntity entity);
}
