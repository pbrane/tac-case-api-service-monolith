package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseAttachmentResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;

public interface RmaCaseAttachmentResponseMapper {
    RmaCaseAttachmentResponseDto mapTo(RmaCaseAttachmentEntity entity);
}