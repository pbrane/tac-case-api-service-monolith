package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseNoteResponseDto;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteResponseMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TacCaseNoteResponseMapperImpl implements TacCaseNoteResponseMapper {

    private final ModelMapper modelMapper;

    public TacCaseNoteResponseMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseNoteResponseDto mapTo(TacCaseNoteEntity entity) {
        return modelMapper.map(entity, TacCaseNoteResponseDto.class);
    }
}