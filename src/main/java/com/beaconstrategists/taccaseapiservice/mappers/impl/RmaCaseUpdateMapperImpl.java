package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.RmaCaseUpdateDto;
import com.beaconstrategists.taccaseapiservice.mappers.Mapper;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RmaCaseUpdateMapperImpl implements Mapper<RmaCaseEntity, RmaCaseUpdateDto> {

    private final ModelMapper modelMapper;

    public RmaCaseUpdateMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

//        modelMapper.typeMap(RmaCaseUpdateDto.class, RmaCaseEntity.class)
//                .addMappings(mapper -> {
//                    // Skip ambiguous or unwanted fields
//                    mapper.skip(RmaCaseEntity::setCaseNumber);
//                });
    }

    @Override
    public RmaCaseUpdateDto mapTo(RmaCaseEntity rmaCaseEntity) {
        RmaCaseUpdateDto rmaCaseUpdateDto = modelMapper.map(rmaCaseEntity, RmaCaseUpdateDto.class);

//        // Optionally, customize the TypeMap for finer control
//        modelMapper.typeMap(RmaCaseUpdateDto.class, RmaCaseEntity.class)
//                .addMappings(mapper -> {
//                    mapper.skip(RmaCaseEntity::setCaseNumber); // Prevent issues with ambiguous fields
//                });


//        // Resolve ambiguity by explicitly mapping caseNumber
//        modelMapper.typeMap(RmaCaseUpdateDto.class, RmaCaseEntity.class)
//                .addMappings(mapper -> mapper.map(
//                        RmaCaseUpdateDto::getCustomerTrackingNumber,
//                        RmaCaseEntity::setCaseNumber
//                ));

//        // Populate rmaCaseIds
//        List<Long> rmaCaseIds = rmaCaseEntity.getRmaCases().stream()
//                .map(RmaCaseEntity::getId)
//                .collect(Collectors.toList());
//        rmaCaseUpdateDto.setRmaCaseIds(rmaCaseIds);

//        // Populate attachmentIds or attachments
//        List<Long> attachmentIds = rCaseEntity.getAttachments().stream()
//                .map(RmaCaseAttachmentEntity::getId)
//                .collect(Collectors.toList());
//        rCaseUpdateDto.setAttachmentIds(attachmentIds);
//
//        List<Long> noteIds = rCaseEntity.getRmaCaseNotes().stream()
//                .map(RmaCaseNoteEntity::getId)
//                .collect(Collectors.toList());
//        rCaseUpdateDto.setNoteIds(noteIds);

        return rmaCaseUpdateDto;
    }

    @Override
    public RmaCaseEntity mapFrom(RmaCaseUpdateDto updateDto) {
        // Configure ModelMapper locally for RmaCaseUpdateDto -> RmaCaseEntity
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        return modelMapper.map(updateDto, RmaCaseEntity.class);
    }

//    public void mapFrom(RmaCaseUpdateDto updateDto, RmaCaseEntity rCaseEntity) {
//        modelMapper.map(updateDto, rCaseEntity);
//    }

    public void map(RmaCaseUpdateDto rmaCaseUpdateDto, RmaCaseEntity rmaCaseEntity) {

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        modelMapper.map(rmaCaseUpdateDto, rmaCaseEntity);
    }
}
