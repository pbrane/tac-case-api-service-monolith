package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseAttachmentResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;

public interface TacCaseAttachmentResponseMapper {
    TacCaseAttachmentResponseDto mapTo(TacCaseAttachmentEntity entity);
}