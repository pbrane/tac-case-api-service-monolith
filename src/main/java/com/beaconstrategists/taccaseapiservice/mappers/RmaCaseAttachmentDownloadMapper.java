package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseAttachmentDownloadDto;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;

public interface RmaCaseAttachmentDownloadMapper {
    RmaCaseAttachmentDownloadDto mapTo(RmaCaseAttachmentEntity entity);
}
