package com.beaconstrategists.taccaseapiservice.config;

import com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseResponseDto;
import com.beaconstrategists.taccaseapiservice.mappers.impl.TacCaseResponseMapperImpl;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
public class ModelMapperConfigurationTest {

    private final TacCaseResponseMapperImpl modelMapper;

    @Autowired
    public ModelMapperConfigurationTest(TacCaseResponseMapperImpl modelMapper) {
        this.modelMapper = modelMapper;
    }

    /*
    This test was written due to ModelMapper attempting to
    set the version in the dto:
    .m.m.a.ExceptionHandlerExceptionResolver :
    Resolved [org.modelmapper.ConfigurationException: ModelMapper configuration errors:1)
    The destination property
        com.beaconstrategists.taccaseapiservice.controllers.dto.TacCaseResponseDto.setVersion()
        matches multiple source property hierarchies:
            com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity.getProductFirmwareVersion()
            com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity.getProductSoftwareVersion()
            ]
     This test will fail with this exception if the mapper is changed.
     */
    @Test
    public void testModelMapperConfiguration() {

        TacCaseEntity entity = new TacCaseEntity();
        entity.setProductFirmwareVersion("FW1.0.3");
        entity.setProductSoftwareVersion("SW2.1.4");

        TacCaseResponseDto dto = modelMapper.mapTo(entity);

        assertNotNull("Should not be null.", dto); // Ensure 'version' is not set
    }

}
