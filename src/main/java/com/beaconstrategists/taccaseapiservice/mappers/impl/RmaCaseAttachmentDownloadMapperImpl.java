package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseAttachmentDownloadDto;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentDownloadMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseAttachmentDownloadMapperImpl implements RmaCaseAttachmentDownloadMapper {

    private final ModelMapper modelMapper;

    public RmaCaseAttachmentDownloadMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseAttachmentDownloadDto mapTo(RmaCaseAttachmentEntity entity) {
        RmaCaseAttachmentDownloadDto dto = new RmaCaseAttachmentDownloadDto();
        dto.setName(entity.getName());
        dto.setMimeType(entity.getMimeType());
        // Convert byte[] to ByteArrayResource or use a streaming approach
        dto.setResource(new ByteArrayResource(entity.getContent()));
        return dto;
    }
}
