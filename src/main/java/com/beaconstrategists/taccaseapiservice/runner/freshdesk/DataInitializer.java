package com.beaconstrategists.taccaseapiservice.runner.freshdesk;

import com.beaconstrategists.taccaseapiservice.services.freshdesk.CompanyService;
import com.beaconstrategists.taccaseapiservice.services.freshdesk.RequesterResponderService;
import com.beaconstrategists.taccaseapiservice  .services.freshdesk.SchemaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private final SchemaService schemaService;
    private final CompanyService companyService;
    private final RequesterResponderService requesterResponderService;

    @Value("${API_SVC_MODE:database}")
    private String apiSvcMode;


    public DataInitializer(SchemaService schemaService, CompanyService companyService, RequesterResponderService requesterResponderService) {
        this.schemaService = schemaService;
        this.companyService = companyService;
        this.requesterResponderService = requesterResponderService;
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

                // Initialize Responder and Responder IDs
                requesterResponderService.initializeResponder();

                requesterResponderService.initializeRequester();
            };
        } else {
            System.out.println("\n");
            return args -> {};
        }

    }

}
