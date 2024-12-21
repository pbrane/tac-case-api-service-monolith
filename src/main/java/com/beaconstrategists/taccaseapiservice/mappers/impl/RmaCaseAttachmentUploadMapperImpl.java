package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseAttachmentUploadDto;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentUploadMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseAttachmentUploadMapperImpl implements RmaCaseAttachmentUploadMapper {

    private final ModelMapper modelMapper;

    public RmaCaseAttachmentUploadMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseAttachmentUploadDto mapTo(RmaCaseAttachmentEntity attachmentEntity) {
        return modelMapper.map(attachmentEntity, RmaCaseAttachmentUploadDto.class);
    }

    @Override
    public RmaCaseAttachmentEntity mapFrom(RmaCaseAttachmentUploadDto rmaCaseAttachmentUploadDto) {
        return modelMapper.map(rmaCaseAttachmentUploadDto, RmaCaseAttachmentEntity.class);
    }

    public void mapFrom(RmaCaseAttachmentUploadDto rmaCaseAttachmentUploadDto, RmaCaseAttachmentEntity attachmentEntity) {
        modelMapper.map(rmaCaseAttachmentUploadDto, attachmentEntity);
    }
}
