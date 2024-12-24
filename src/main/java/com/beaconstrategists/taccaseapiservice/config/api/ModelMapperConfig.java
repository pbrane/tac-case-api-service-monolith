package com.beaconstrategists.taccaseapiservice.config.api;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ModelMapperConfig {

    /*
    Fixme: Why have this?
     */
    @Bean
    @Qualifier("basicModelMapper")
    @Primary
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
