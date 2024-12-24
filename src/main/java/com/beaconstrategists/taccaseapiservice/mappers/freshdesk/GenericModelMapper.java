package com.beaconstrategists.taccaseapiservice.mappers.freshdesk;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

//fixme: I don't think this is any better than the ModelMapperConfig
//fixme: other than to add the LocalDate converter
//fixme: look at getting rid of this class
@Component
public class GenericModelMapper {

    private final ModelMapper modelMapper;

    public GenericModelMapper(@Qualifier("freshdeskModelMapper") ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <S, D> D map(S source, Class<D> destinationClass) {
        configureMapper(); // Optional: Apply custom configuration
        return modelMapper.map(source, destinationClass);
    }

    public <S, D> void map(S source, D destination) {
        configureMapper(); // Optional: Apply custom configuration
        modelMapper.map(source, destination);
    }

    private void configureMapper() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setAmbiguityIgnored(true);

        // Add custom converter for LocalDate -> OffsetDateTime
        modelMapper.addConverter(context -> {
            LocalDate source = context.getSource();
            return source != null ? source.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
        }, LocalDate.class, OffsetDateTime.class);
    }
}
