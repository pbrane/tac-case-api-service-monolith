package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.dtos.RmaCaseResponseDto;
import com.beaconstrategists.taccaseapiservice.mappers.Mapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseAttachmentEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RmaCaseResponseMapperImpl implements Mapper<RmaCaseEntity, RmaCaseResponseDto> {

    private final ModelMapper modelMapper;

    public RmaCaseResponseMapperImpl(@Qualifier("basicModelMapper") ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseResponseDto mapTo(RmaCaseEntity rmaCaseEntity) {
        RmaCaseResponseDto rmaCaseResponseDto = modelMapper.map(rmaCaseEntity, RmaCaseResponseDto.class);

        // Populate attachmentIds or attachments
        List<Long> attachmentIds = rmaCaseEntity.getAttachments().stream()
                .map(RmaCaseAttachmentEntity::getId)
                .collect(Collectors.toList());
        rmaCaseResponseDto.setAttachmentIds(attachmentIds);

        return rmaCaseResponseDto;
    }

    @Override
    public RmaCaseEntity mapFrom(RmaCaseResponseDto rmaCaseResponseDto) {
        return modelMapper.map(rmaCaseResponseDto, RmaCaseEntity.class);
    }

    public void mapFrom(RmaCaseResponseDto rmaCaseResponseDto, RmaCaseEntity rmaCaseEntity) {
        modelMapper.map(rmaCaseResponseDto, rmaCaseEntity);
    }
}
