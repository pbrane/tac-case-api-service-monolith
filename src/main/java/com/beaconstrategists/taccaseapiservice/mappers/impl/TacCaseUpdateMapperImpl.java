package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseUpdateDto;
import com.beaconstrategists.taccaseapiservice.mappers.Mapper;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TacCaseUpdateMapperImpl implements Mapper<TacCaseEntity, TacCaseUpdateDto> {

    private final ModelMapper modelMapper;

    public TacCaseUpdateMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseUpdateDto mapTo(TacCaseEntity tacCaseEntity) {
        return modelMapper.map(tacCaseEntity, TacCaseUpdateDto.class);
    }

    @Override
    public TacCaseEntity mapFrom(TacCaseUpdateDto updateDto) {

        // Configure ModelMapper locally for TacCaseUpdateDto -> TacCaseEntity
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        return modelMapper.map(updateDto, TacCaseEntity.class);
    }

    public void map(TacCaseUpdateDto tacCaseUpdateDto, TacCaseEntity tacCaseEntity) {

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        modelMapper.map(tacCaseUpdateDto, tacCaseEntity);
    }
}
