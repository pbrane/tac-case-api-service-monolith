package com.beaconstrategists.taccaseapiservice.services.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchemaService {

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
    public List<JsonNode> fetchSchemas() {
        JsonNode response = restClient.get()
                .uri("/custom_objects/schemas")
                .retrieve()
                .body(JsonNode.class);

        // Extract the `schemas` array from the response JSON
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
        var schemas = fetchSchemas();
        schemas.forEach(schema -> {
            String name = schema.get("name").asText();
            schemaMap.put(name, schema);
        });
    }

    /**
     * Retrieves a schema by its name.
     *
     * @param name The name of the schema.
     * @return The schema as a JsonNode, or null if not found.
     */
    public JsonNode getSchemaByName(String name) {
        return schemaMap.get(name);
    }

    /**
     * Retrieves the ID of a schema by its name.
     *
     * @param name The name of the schema.
     * @return The ID of the schema as a String, or null if not found.
     */
    public String getSchemaIdByName(String name) {
        JsonNode schema = schemaMap.get(name);
        return schema != null ? schema.get("id").asText() : null;
    }
}
