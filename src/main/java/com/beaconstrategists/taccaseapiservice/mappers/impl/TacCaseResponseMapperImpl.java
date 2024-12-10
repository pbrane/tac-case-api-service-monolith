package com.beaconstrategists.taccaseapiservice.mappers.impl;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseResponseDto;
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
public class TacCaseResponseMapperImpl implements Mapper<TacCaseEntity, TacCaseResponseDto> {

    private final ModelMapper modelMapper;

    public TacCaseResponseMapperImpl(ModelMapper modelMapper) {

//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration()
//                .setFieldMatchingEnabled(true)
//                .setAmbiguityIgnored(true);
//        modelMapper.typeMap(TacCaseEntity.class, TacCaseResponseDto.class)
//                .addMappings(mapper -> mapper.skip(TacCaseResponseDto::setVersion));

        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseResponseDto mapTo(TacCaseEntity tacCaseEntity) {

        TacCaseResponseDto tacCaseResponseDto = modelMapper.map(tacCaseEntity, TacCaseResponseDto.class);

        // Populate rmaCaseIds
        List<Long> rmaCaseIds = tacCaseEntity.getRmaCases().stream()
                .map(RmaCaseEntity::getId)
                .collect(Collectors.toList());
        tacCaseResponseDto.setRmaCaseIds(rmaCaseIds);

        // Populate attachmentIds or attachments
        List<Long> attachmentIds = tacCaseEntity.getAttachments().stream()
                .map(TacCaseAttachmentEntity::getId)
                .collect(Collectors.toList());
        tacCaseResponseDto.setAttachmentIds(attachmentIds);

        List<Long> noteIds = tacCaseEntity.getTacCaseNotes().stream()
                .map(TacCaseNoteEntity::getId)
                .collect(Collectors.toList());
        tacCaseResponseDto.setNoteIds(noteIds);

        return tacCaseResponseDto;
    }

    @Override
    public TacCaseEntity mapFrom(TacCaseResponseDto tacCaseResponseDto) {

        //fixme: should we be doing this here?
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        return modelMapper.map(tacCaseResponseDto, TacCaseEntity.class);
    }

    public void mapFrom(TacCaseResponseDto tacCaseResponseDto, TacCaseEntity tacCaseEntity) {
        modelMapper.map(tacCaseResponseDto, tacCaseEntity);
    }
}
