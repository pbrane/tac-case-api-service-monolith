package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseAttachmentDownloadDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;

public interface TacCaseAttachmentDownloadMapper {
    TacCaseAttachmentDownloadDto mapTo(TacCaseAttachmentEntity entity);
}
