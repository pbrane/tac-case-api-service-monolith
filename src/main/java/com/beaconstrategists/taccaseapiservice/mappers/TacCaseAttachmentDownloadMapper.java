package com.beaconstrategists.taccaseapiservice.mappers;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseAttachmentDownloadDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;

public interface TacCaseAttachmentDownloadMapper {
    TacCaseAttachmentDownloadDto mapTo(TacCaseAttachmentEntity entity);
}
