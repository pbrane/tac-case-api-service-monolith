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

    @Value("${FD_CUSTOMER_NAME:Beacon}")
    private String requiredCompany;

    @Value("${FD_TAC_CASE_SCHEMA_NAME:TAC Cases}")
    private String tacCaseSchemaName;

    @Value("${FD_RMA_CASE_SCHEMA_NAME:RMA Cases}")
    private String rmaCaseSchemaName;


    public DataInitializer(SchemaService schemaService, CompanyService companyService) {
        this.schemaService = schemaService;
        this.companyService = companyService;
    }

    @Bean
    public ApplicationRunner initializeData() {

        System.out.println("\n\tInitializing data...");
        System.out.println("\tAPI Mode: " + apiSvcMode);

        if (apiSvcMode.equalsIgnoreCase("freshdesk")) {
            System.out.println("\tCompany Name: " + requiredCompany);
            System.out.println("\n");
            return args -> {
                // Initialize Schemas
                schemaService.initializeSchemas();
                validateSchemas();

                // Initialize Companies
                companyService.initializeCompanies();
                validateCompany();
            };
        } else {
            System.out.println("\n");
            return args -> {};
        }

    }

    private void validateSchemas() {
        JsonNode tacSchema = schemaService.getSchemaByName(tacCaseSchemaName);
        JsonNode rmaSchema = schemaService.getSchemaByName(rmaCaseSchemaName);

        if (tacSchema == null || rmaSchema == null) {
            System.err.println("ERROR: Required schemas '"+tacCaseSchemaName+"' and '"+rmaCaseSchemaName+"' are missing in Freshdesk.");
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
