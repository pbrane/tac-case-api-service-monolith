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

        // Configure ModelMapper locally for RmaCaseCreateDto -> RmaCaseEntity
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        // Skip, mapper wants to map the tacCaseId to this field!
        modelMapper.typeMap(RmaCaseCreateDto.class, RmaCaseEntity.class)
                .addMappings(mapper -> mapper.skip(RmaCaseEntity::setId));
    }

    @Override
    public RmaCaseCreateDto mapTo(RmaCaseEntity rmaCaseEntity) {
        RmaCaseCreateDto rmaCaseCreateDto = modelMapper.map(rmaCaseEntity, RmaCaseCreateDto.class);

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

        return rmaCaseCreateDto;
    }

    @Override
    public RmaCaseEntity mapFrom(RmaCaseCreateDto createDto) {
        return modelMapper.map(createDto, RmaCaseEntity.class);
    }

//    public void mapFrom(TacCaseCreateDto createDto, TacCaseEntity tacCaseEntity) {
//        modelMapper.map(createDto, tacCaseEntity);
//    }
}
