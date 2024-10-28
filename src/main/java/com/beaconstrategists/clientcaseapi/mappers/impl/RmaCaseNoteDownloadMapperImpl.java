package com.beaconstrategists.clientcaseapi.mappers.impl;

import com.beaconstrategists.clientcaseapi.controllers.dto.RmaCaseNoteDownloadDto;
import com.beaconstrategists.clientcaseapi.mappers.RmaCaseNoteDownloadMapper;
import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseNoteDownloadMapperImpl implements RmaCaseNoteDownloadMapper {

    private final ModelMapper modelMapper;

    public RmaCaseNoteDownloadMapperImpl(ModelMapper modelMapper) {

        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseNoteDownloadDto mapTo(RmaCaseNoteEntity entity) {
        RmaCaseNoteDownloadDto dto = new RmaCaseNoteDownloadDto();
        dto.setId(entity.getId());
        dto.setAuthor(entity.getAuthor());
        dto.setRmaCaseId(entity.getRmaCase().getId());
        dto.setDate(entity.getDate());
        dto.setText(entity.getText());
        return dto;
    }
}