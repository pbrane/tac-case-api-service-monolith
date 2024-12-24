package com.beaconstrategists.taccaseapiservice.config;

import com.beaconstrategists.taccaseapiservice.services.RmaCaseService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.impl.FreshDeskRmaCaseService;
import com.beaconstrategists.taccaseapiservice.services.impl.RmaCaseServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RmaCaseServiceConfig {

    @Value("${API_SVC_MODE:Database}") // Default to JPA if not specified
    private String apiSvcMode;

    //fixme: may rename TacCaseServiceImpl
    @Bean
    public RmaCaseService rmaCaseService(RmaCaseServiceImpl jpaRmaCaseService, FreshDeskRmaCaseService freshdeskCaseService) {
        if ("Freshdesk".equalsIgnoreCase(apiSvcMode)) {
            return freshdeskCaseService;
        } else {
            return jpaRmaCaseService;
        }
    }
}
