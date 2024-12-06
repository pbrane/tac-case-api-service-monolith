package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseCreateDto;
import com.beaconstrategists.taccaseapiservice.mappers.Mapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseNoteEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TacCaseCreateMapperImpl implements Mapper<TacCaseEntity, TacCaseCreateDto> {

    private final ModelMapper modelMapper;

    public TacCaseCreateMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Configure ModelMapper locally for TacCaseCreateDto -> TacCaseEntity
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

//        modelMapper.typeMap(TacCaseCreateDto.class, TacCaseEntity.class)
//                .addMappings(mapper -> {
//                    // Skip ambiguous or unwanted fields
//                    mapper.skip(TacCaseEntity::setCaseNumber);
//                });
    }

    @Override
    public TacCaseCreateDto mapTo(TacCaseEntity tacCaseEntity) {
        TacCaseCreateDto tacCaseCreateDto = modelMapper.map(tacCaseEntity, TacCaseCreateDto.class);

//        // Optionally, customize the TypeMap for finer control
//        modelMapper.typeMap(TacCaseCreateDto.class, TacCaseEntity.class)
//                .addMappings(mapper -> {
//                    mapper.skip(TacCaseEntity::setCaseNumber); // Prevent issues with ambiguous fields
//                });


//        // Resolve ambiguity by explicitly mapping caseNumber
//        modelMapper.typeMap(TacCaseCreateDto.class, TacCaseEntity.class)
//                .addMappings(mapper -> mapper.map(
//                        TacCaseCreateDto::getCustomerTrackingNumber,
//                        TacCaseEntity::setCaseNumber
//                ));

//        // Populate rmaCaseIds
//        List<Long> rmaCaseIds = tacCaseEntity.getRmaCases().stream()
//                .map(RmaCaseEntity::getId)
//                .collect(Collectors.toList());
//        tacCaseCreateDto.setRmaCaseIds(rmaCaseIds);

//        // Populate attachmentIds or attachments
//        List<Long> attachmentIds = tacCaseEntity.getAttachments().stream()
//                .map(TacCaseAttachmentEntity::getId)
//                .collect(Collectors.toList());
//        tacCaseCreateDto.setAttachmentIds(attachmentIds);
//
//        List<Long> noteIds = tacCaseEntity.getTacCaseNotes().stream()
//                .map(TacCaseNoteEntity::getId)
//                .collect(Collectors.toList());
//        tacCaseCreateDto.setNoteIds(noteIds);

        return tacCaseCreateDto;
    }

    @Override
    public TacCaseEntity mapFrom(TacCaseCreateDto createDto) {
        return modelMapper.map(createDto, TacCaseEntity.class);
    }

//    public void mapFrom(TacCaseCreateDto createDto, TacCaseEntity tacCaseEntity) {
//        modelMapper.map(createDto, tacCaseEntity);
//    }
}
