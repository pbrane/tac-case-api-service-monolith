package com.beaconstrategists.taccaseapiservice.services.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompanyService {

    private final RestClient restClient;
    private final Map<String, String> companyMap = new ConcurrentHashMap<>();

    @Value("${FD_CUSTOMER_NAME:Beacon}")
    private String requiredCompany;

    public CompanyService(@Qualifier("camelCaseRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<JsonNode> fetchCompanies() {
        JsonNode response = restClient.get()
                .uri("/companies")
                .retrieve()
                .body(JsonNode.class);

        List<JsonNode> companies = new ArrayList<>();
        assert response != null;
        response.forEach(companies::add);
        return companies;
    }

    public void initializeCompanies() {
        System.out.println("\n\tInitializing Company Map for: " + requiredCompany);
        System.out.println("\n");

        var companies = fetchCompanies();
        companies.forEach(company -> {
            String name = company.get("name").asText();
            String id = company.get("id").asText();
            companyMap.put(name, id);
        });

        /*
          Go ahead and validate schemas here.
         */

        if (companyMap.isEmpty()) {
            throw new IllegalStateException("No companies found");
        } else if (!companyMap.containsKey(requiredCompany)) {
            throw new IllegalStateException("The required company is not found");
        }

    }

    public String getCompanyIdByName(String name) {
        return companyMap.get(name);
    }
}
