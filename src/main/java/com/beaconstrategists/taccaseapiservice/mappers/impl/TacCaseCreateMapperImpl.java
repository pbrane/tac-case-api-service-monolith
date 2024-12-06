package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseCreateDto;
import com.beaconstrategists.taccaseapiservice.mappers.Mapper;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TacCaseCreateMapperImpl implements Mapper<TacCaseEntity, TacCaseCreateDto> {

    private final ModelMapper modelMapper;

    public TacCaseCreateMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseCreateDto mapTo(TacCaseEntity tacCaseEntity) {
        return modelMapper.map(tacCaseEntity, TacCaseCreateDto.class);
    }

    @Override
    public TacCaseEntity mapFrom(TacCaseCreateDto createDto) {
        
        // Configure ModelMapper locally for TacCaseCreateDto -> TacCaseEntity
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        return modelMapper.map(createDto, TacCaseEntity.class);
    }

    public void mapFrom(TacCaseCreateDto createDto, TacCaseEntity tacCaseEntity) {
        modelMapper.map(createDto, tacCaseEntity);
    }
}
