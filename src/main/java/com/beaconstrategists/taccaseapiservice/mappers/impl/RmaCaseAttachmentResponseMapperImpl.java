package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseAttachmentResponseDto;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseAttachmentResponseMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseAttachmentResponseMapperImpl implements RmaCaseAttachmentResponseMapper {

    private final ModelMapper modelMapper;

    public RmaCaseAttachmentResponseMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseAttachmentResponseDto mapTo(RmaCaseAttachmentEntity entity) {
        return modelMapper.map(entity, RmaCaseAttachmentResponseDto.class);
    }
}