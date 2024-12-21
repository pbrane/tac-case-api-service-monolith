package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseAttachmentResponseDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;

public interface TacCaseAttachmentResponseMapper {
    TacCaseAttachmentResponseDto mapTo(TacCaseAttachmentEntity entity);
}