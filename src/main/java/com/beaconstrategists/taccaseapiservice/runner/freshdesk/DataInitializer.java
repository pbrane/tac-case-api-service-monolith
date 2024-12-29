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

    @Value("${FD_CUSTOMER_NAME:Beacon}")
    private String requiredCompany;

    public DataInitializer(SchemaService schemaService, CompanyService companyService) {
        this.schemaService = schemaService;
        this.companyService = companyService;
    }

    @Bean
    public ApplicationRunner initializeData() {
        return args -> {
            // Initialize Schemas
            schemaService.initializeSchemas();
            validateSchemas();

            // Initialize Companies
            companyService.initializeCompanies();
            validateCompany();
        };
    }

    private void validateSchemas() {
        JsonNode tacSchema = schemaService.getSchemaByName("TAC Cases");
        JsonNode rmaSchema = schemaService.getSchemaByName("RMA Cases");

        if (tacSchema == null || rmaSchema == null) {
            System.err.println("ERROR: Required schemas 'TAC Cases' and 'RMA Cases' are missing.");
            System.exit(1);
        }

        System.out.println("Schemas validated successfully.");
    }

    private void validateCompany() {
        String companyId = companyService.getCompanyIdByName(requiredCompany);

        if (companyId == null) {
            System.err.println("ERROR: Required company '" + requiredCompany + "' is missing.");
            System.exit(1);
        }

        System.out.println("Company validated successfully. Company ID: " + companyId);
    }
}
