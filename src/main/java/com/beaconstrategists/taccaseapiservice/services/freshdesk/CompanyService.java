package com.beaconstrategists.taccaseapiservice.services.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompanyService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Map<String, String> companyMap = new ConcurrentHashMap<>();

    public CompanyService(@Qualifier("camelCaseRestClient") RestClient restClient, @Qualifier("camelCaseObjectMapper") ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
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
        var companies = fetchCompanies();
        companies.forEach(company -> {
            String name = company.get("name").asText();
            String id = company.get("id").asText();
            companyMap.put(name, id);
        });
    }

    public String getCompanyIdByName(String name) {
        return companyMap.get(name);
    }
}
