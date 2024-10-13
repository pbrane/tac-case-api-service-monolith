package com.beaconstrategists.clientcaseapi.mappers.impl;

import com.beaconstrategists.clientcaseapi.controllers.dto.TacCaseDto;
import com.beaconstrategists.clientcaseapi.mappers.Mapper;
import com.beaconstrategists.clientcaseapi.model.entities.RmaCaseEntity;
import com.beaconstrategists.clientcaseapi.model.entities.TacCaseAttachmentEntity;
import com.beaconstrategists.clientcaseapi.model.entities.TacCaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TacCaseMapperImpl implements Mapper<TacCaseEntity, TacCaseDto> {

    private final ModelMapper modelMapper;

    public TacCaseMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TacCaseDto mapTo(TacCaseEntity tacCaseEntity) {
        TacCaseDto tacCaseDto = modelMapper.map(tacCaseEntity, TacCaseDto.class);

        // Populate rmaCaseIds
        List<Long> rmaCaseIds = tacCaseEntity.getRmaCases().stream()
                .map(RmaCaseEntity::getId)
                .collect(Collectors.toList());
        tacCaseDto.setRmaCaseIds(rmaCaseIds);

        // Populate attachmentIds or attachments
        List<Long> attachmentIds = tacCaseEntity.getAttachments().stream()
                .map(TacCaseAttachmentEntity::getId)
                .collect(Collectors.toList());
        tacCaseDto.setAttachmentIds(attachmentIds);

        return tacCaseDto;
    }

    @Override
    public TacCaseEntity mapFrom(TacCaseDto tacCaseDto) {
        return modelMapper.map(tacCaseDto, TacCaseEntity.class);
    }

    public void mapFrom(TacCaseDto tacCaseDto, TacCaseEntity tacCaseEntity) {
        modelMapper.map(tacCaseDto, tacCaseEntity);
    }
}