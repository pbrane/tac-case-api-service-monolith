package com.beaconstrategists.taccaseapiservice.services.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class SchemaService {

    @Value("${FD_TAC_CASE_SCHEMA_NAME:TAC Cases}")
    private String tacCaseSchemaName;

    @Value("${FD_RMA_CASE_SCHEMA_NAME:RMA Cases}")
    private String rmaCaseSchemaName;


    private final RestClient restClient;
    private final Map<String, JsonNode> schemaMap = new ConcurrentHashMap<>();

    public SchemaService(@Qualifier("snakeCaseRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Fetches all schemas from the Freshdesk API.
     * The API response contains a `schemas` key that holds the list of schemas.
     *
     * @return List of schemas as JsonNode objects.
     */
    private List<JsonNode> fetchSchemas() {
        JsonNode response = restClient.get()
                .uri("/custom_objects/schemas")
                .retrieve()
                .body(JsonNode.class);

        // Extract the "schemas" array from the response JSON
        List<JsonNode> schemas = new ArrayList<>();
        assert response != null;
        if (response.has("schemas")) {
            response.get("schemas").forEach(schemas::add);
        }
        return schemas;
    }

    /**
     * Initializes the schemas by storing them in a map for easy access.
     */
    public void initializeSchemas() {
        System.out.println("\n\tInitializing Schema Service... TAC: " + tacCaseSchemaName +", RMA: " + rmaCaseSchemaName);
        System.out.println("\n");

        var schemas = fetchSchemas();
        schemas.forEach(schema -> {
            String name = schema.get("name").asText();
            schemaMap.put(name, schema);
        });

        /*
          Go ahead and validate schemas here.
         */
        if (schemaMap.isEmpty()) {
            System.out.println("No schemas found");
            throw new IllegalStateException("No schemas found");
        } else if (!schemaMap.containsKey(tacCaseSchemaName)) {
            System.out.println("Schema not found: " + tacCaseSchemaName);
            throw new IllegalStateException(tacCaseSchemaName+" Schema not found!");
        } else if (!schemaMap.containsKey(rmaCaseSchemaName)) {
            System.out.println("Schema not found: " + rmaCaseSchemaName);
            throw new IllegalStateException(rmaCaseSchemaName+" Schema not found!");
        }
    }

    public String getTacCaseSchemaId() {
        JsonNode schema = schemaMap.get(tacCaseSchemaName);
        return schema != null ? schema.get("id").asText() : null;
    }

    public String getRMACaseSchemaId() {
        JsonNode schema = schemaMap.get(rmaCaseSchemaName);
        return schema != null ? schema.get("id").asText() : null;
    }

}
