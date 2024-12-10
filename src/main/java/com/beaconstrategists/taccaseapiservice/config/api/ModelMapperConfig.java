package com.beaconstrategists.taccaseapiservice.config.api;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    /*
    Fixme: Why have this?
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
