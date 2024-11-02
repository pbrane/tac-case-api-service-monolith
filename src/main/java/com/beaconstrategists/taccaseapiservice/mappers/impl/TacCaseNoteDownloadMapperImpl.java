package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseNoteDownloadDto;
import com.beaconstrategists.taccaseapiservice.mappers.TacCaseNoteDownloadMapper;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TacCaseNoteDownloadMapperImpl implements TacCaseNoteDownloadMapper {

    private final ModelMapper modelMapper;

    public TacCaseNoteDownloadMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseNoteDownloadDto mapTo(TacCaseNoteEntity entity) {
        TacCaseNoteDownloadDto dto = new TacCaseNoteDownloadDto();
        dto.setId(entity.getId());
        dto.setAuthor(entity.getAuthor());
        dto.setTacCaseId(entity.getTacCase().getId());
        dto.setDate(entity.getDate());
        dto.setText(entity.getText());
        return dto;
    }
}