package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.RmaCaseCreateDto;
import com.beaconstrategists.taccaseapiservice.mappers.Mapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseCreateMapperImpl implements Mapper<RmaCaseEntity, RmaCaseCreateDto> {

    private final ModelMapper modelMapper;

    public RmaCaseCreateMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RmaCaseCreateDto mapTo(RmaCaseEntity rmaCaseEntity) {
        return modelMapper.map(rmaCaseEntity, RmaCaseCreateDto.class);
    }

    @Override
    public RmaCaseEntity mapFrom(RmaCaseCreateDto createDto) {

        // Configure ModelMapper locally for RmaCaseCreateDto -> RmaCaseEntity
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        // Skip, mapper wants to map the tacCaseId to this field!
        modelMapper.typeMap(RmaCaseCreateDto.class, RmaCaseEntity.class)
                .addMappings(mapper -> mapper.skip(RmaCaseEntity::setId));

        return modelMapper.map(createDto, RmaCaseEntity.class);
    }

    public void mapFrom(RmaCaseCreateDto createDto, RmaCaseEntity tacCaseEntity) {
        modelMapper.map(createDto, tacCaseEntity);
    }
}
