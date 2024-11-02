package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseAttachmentResponseDto;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseAttachmentResponseMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TacCaseAttachmentResponseMapperImpl implements TacCaseAttachmentResponseMapper {

    private final ModelMapper modelMapper;

    public TacCaseAttachmentResponseMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseAttachmentResponseDto mapTo(TacCaseAttachmentEntity entity) {
        return modelMapper.map(entity, TacCaseAttachmentResponseDto.class);
    }
}