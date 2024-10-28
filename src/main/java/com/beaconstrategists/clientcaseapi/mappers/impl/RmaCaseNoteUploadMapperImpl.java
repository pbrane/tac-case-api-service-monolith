package com.beaconstrategists.clientcaseapi.mappers.impl;

import com.beaconstrategists.clientcaseapi.controllers.dto.RmaCaseNoteUploadDto;
import com.beaconstrategists.clientcaseapi.mappers.RmaCaseNoteUploadMapper;
import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseNoteUploadMapperImpl implements RmaCaseNoteUploadMapper {

    private final ModelMapper modelMapper;

    public RmaCaseNoteUploadMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseNoteUploadDto mapTo(RmaCaseNoteEntity noteEntity) {
        return modelMapper.map(noteEntity, RmaCaseNoteUploadDto.class);
    }

    @Override
    public RmaCaseNoteEntity mapFrom(RmaCaseNoteUploadDto noteUploadDto) {
        return modelMapper.map(noteUploadDto, RmaCaseNoteEntity.class);
    }

    public void mapFrom(RmaCaseNoteUploadDto noteUploadDto, RmaCaseNoteEntity noteEntity) {
        modelMapper.map(noteUploadDto, noteEntity);
    }
}
