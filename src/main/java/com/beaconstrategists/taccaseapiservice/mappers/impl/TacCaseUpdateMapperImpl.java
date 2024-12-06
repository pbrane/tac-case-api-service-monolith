package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseCreateDto;
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

//        modelMapper.typeMap(TacCaseUpdateDto.class, TacCaseEntity.class)
//                .addMappings(mapper -> {
//                    // Skip ambiguous or unwanted fields
//                    mapper.skip(TacCaseEntity::setCaseNumber);
//                });
    }

    @Override
    public TacCaseUpdateDto mapTo(TacCaseEntity tacCaseEntity) {
        TacCaseUpdateDto tacCaseUpdateDto = modelMapper.map(tacCaseEntity, TacCaseUpdateDto.class);

//        // Optionally, customize the TypeMap for finer control
//        modelMapper.typeMap(TacCaseUpdateDto.class, TacCaseEntity.class)
//                .addMappings(mapper -> {
//                    mapper.skip(TacCaseEntity::setCaseNumber); // Prevent issues with ambiguous fields
//                });


//        // Resolve ambiguity by explicitly mapping caseNumber
//        modelMapper.typeMap(TacCaseUpdateDto.class, TacCaseEntity.class)
//                .addMappings(mapper -> mapper.map(
//                        TacCaseUpdateDto::getCustomerTrackingNumber,
//                        TacCaseEntity::setCaseNumber
//                ));

//        // Populate rmaCaseIds
//        List<Long> rmaCaseIds = tacCaseEntity.getRmaCases().stream()
//                .map(RmaCaseEntity::getId)
//                .collect(Collectors.toList());
//        tacCaseUpdateDto.setRmaCaseIds(rmaCaseIds);

//        // Populate attachmentIds or attachments
//        List<Long> attachmentIds = tacCaseEntity.getAttachments().stream()
//                .map(TacCaseAttachmentEntity::getId)
//                .collect(Collectors.toList());
//        tacCaseUpdateDto.setAttachmentIds(attachmentIds);
//
//        List<Long> noteIds = tacCaseEntity.getTacCaseNotes().stream()
//                .map(TacCaseNoteEntity::getId)
//                .collect(Collectors.toList());
//        tacCaseUpdateDto.setNoteIds(noteIds);

        return tacCaseUpdateDto;
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

//    public void mapFrom(TacCaseUpdateDto updateDto, TacCaseEntity tacCaseEntity) {
//        modelMapper.map(updateDto, tacCaseEntity);
//    }

    public void map(TacCaseUpdateDto tacCaseUpdateDto, TacCaseEntity tacCaseEntity) {

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        modelMapper.map(tacCaseUpdateDto, tacCaseEntity);
    }
}
