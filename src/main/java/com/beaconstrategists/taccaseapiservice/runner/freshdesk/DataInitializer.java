package com.beaconstrategists.taccaseapiservice.runner.freshdesk;

import com.beaconstrategists.taccaseapiservice.services.freshdesk.CompanyService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.ResponderService;
import com.beaconstrategists.taccaseapiservice  .services.freshdesk.SchemaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private final SchemaService schemaService;
    private final CompanyService companyService;
    private final ResponderService responderService;

    @Value("${API_SVC_MODE:database}")
    private String apiSvcMode;


    public DataInitializer(SchemaService schemaService, CompanyService companyService, ResponderService responderService) {
        this.schemaService = schemaService;
        this.companyService = companyService;
        this.responderService = responderService;
    }

    @Bean
    public ApplicationRunner initializeData() {

        System.out.println("\n\tInitializing data...");
        System.out.println("\tAPI Mode: " + apiSvcMode);

        if (apiSvcMode.equalsIgnoreCase("freshdesk")) {
            return args -> {
                // Initialize Schemas
                schemaService.initializeSchemas();

                // Initialize Companies
                companyService.initializeCompanies();

                // Initialize Responder ID
                responderService.initializeResponder();
            };
        } else {
            System.out.println("\n");
            return args -> {};
        }

    }

}
