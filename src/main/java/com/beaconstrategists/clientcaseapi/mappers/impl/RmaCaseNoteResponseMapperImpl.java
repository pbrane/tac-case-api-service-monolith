package com.beaconstrategists.clientcaseapi.mappers.impl;

import com.beaconstrategists.clientcaseapi.controllers.dto.RmaCaseNoteResponseDto;
import com.beaconstrategists.clientcaseapi.mappers.RmaCaseNoteResponseMapper;
import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseNoteEntity;
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