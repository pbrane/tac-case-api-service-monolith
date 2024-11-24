package com.beaconstrategists.taccaseapiservice.config.api;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseAttachmentUploadDto;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseAttachmentEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Example: Customize mapping for AttachmentDetailDto
        modelMapper.addMappings(new PropertyMap<TacCaseAttachmentEntity, TacCaseAttachmentUploadDto>() {
            @Override
            protected void configure() {
                // Customize mappings if necessary
                // For example, map nested TacCaseEntity to tacCaseId in DTO
                // map(source.getTacCase().getId(), destination.getTacCaseId());
            }
        });

        return modelMapper;
    }
}
