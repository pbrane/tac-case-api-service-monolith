package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.dtos.TacCaseNoteUploadDto;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteUploadMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TacCaseNoteUploadMapperImpl implements TacCaseNoteUploadMapper {

    private final ModelMapper modelMapper;

    public TacCaseNoteUploadMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseNoteUploadDto mapTo(TacCaseNoteEntity noteEntity) {
        return modelMapper.map(noteEntity, TacCaseNoteUploadDto.class);
    }

    @Override
    public TacCaseNoteEntity mapFrom(TacCaseNoteUploadDto tacCaseNoteUploadDto) {
        return modelMapper.map(tacCaseNoteUploadDto, TacCaseNoteEntity.class);
    }

    public void mapFrom(TacCaseNoteUploadDto tacCaseNoteUploadDto, TacCaseNoteEntity noteEntity) {
        modelMapper.map(tacCaseNoteUploadDto, noteEntity);
    }
}
