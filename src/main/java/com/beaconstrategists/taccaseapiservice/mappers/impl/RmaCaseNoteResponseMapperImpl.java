package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.RmaCaseNoteResponseDto;
import com.beaconstrategists.taccaseapiservice.mappers.RmaCaseNoteResponseMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseNoteResponseMapperImpl implements RmaCaseNoteResponseMapper {

    private final ModelMapper modelMapper;

    public RmaCaseNoteResponseMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseNoteResponseDto mapTo(RmaCaseNoteEntity entity) {
        return modelMapper.map(entity, RmaCaseNoteResponseDto.class);
    }
}