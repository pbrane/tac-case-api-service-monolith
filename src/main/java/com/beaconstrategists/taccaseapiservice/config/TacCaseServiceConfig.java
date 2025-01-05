package com.beaconstrategists.taccaseapiservice.config;

import com.beaconstrategists.taccaseapiservice.services.TacCaseService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.impl.FreshDeskTacCaseService;
import com.beaconstrategists.taccaseapiservice.services.impl.TacCaseServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TacCaseServiceConfig {

    @Value("${API_SVC_MODE:database}")
    private String apiSvcMode;

    @Bean
    public TacCaseService tacCaseService(TacCaseServiceImpl jpaTacCaseService, FreshDeskTacCaseService freshdeskCaseService) {
        if ("freshdesk".equalsIgnoreCase(apiSvcMode)) {
            return freshdeskCaseService;
        } else {
            return jpaTacCaseService;
        }
    }
}
