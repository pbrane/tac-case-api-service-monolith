package com.beaconstrategists.taccaseapiservice.config.api;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
//
//        // Global settings
//        modelMapper.getConfiguration()
//                .setSkipNullEnabled(true) // Skip null values
//                .setFieldMatchingEnabled(true) // Match fields by name
//                .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        return modelMapper;
    }
}
