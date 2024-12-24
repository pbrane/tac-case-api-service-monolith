package com.beaconstrategists.taccaseapiservice.config.freshdesk;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//fixme: do I need this bean?
@Configuration
public class FreshdeskModelMapperConfig {

    @Bean
    @Qualifier("freshdeskModelMapper")
    public ModelMapper freshDeskModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Skip null values
                .setFieldMatchingEnabled(true) // Enable field matching
                .setAmbiguityIgnored(true); // Ignore ambiguous mappings
        return modelMapper;
    }
}