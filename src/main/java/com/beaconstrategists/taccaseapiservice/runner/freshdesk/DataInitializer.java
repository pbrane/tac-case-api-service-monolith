package com.beaconstrategists.taccaseapiservice.runner.freshdesk;

import com.beaconstrategists.taccaseapiservice.services.freshdesk.CompanyService;
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

    @Value("${API_SVC_MODE:database}")
    private String apiSvcMode;


    public DataInitializer(SchemaService schemaService, CompanyService companyService) {
        this.schemaService = schemaService;
        this.companyService = companyService;
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
            };
        } else {
            System.out.println("\n");
            return args -> {};
        }

    }

}
