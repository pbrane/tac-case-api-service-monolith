package com.beaconstrategists.taccaseapiservice.services.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class RequesterResponderService {

    private final RestClient restClient;
    private final Map<String, String> responderMap = new ConcurrentHashMap<>();
    private String defaultResponderId;

    @Value("${FD_API_KEY:0123456789ABCDEFGHI}")
    private String freshdeskApiKey;

    @Value("${FD_DEFAULT_REQUESTER_ID:123456}")
    private String defaultRequesterId;

    public RequesterResponderService(@Qualifier("camelCaseRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void initializeResponder() {

        if (freshdeskApiKey.equals("0123456789ABCDEFGHI")) {
            throw new IllegalStateException("No API Key provided.");
        }

        System.out.println("\n\tInitializing Responder using API Key: " + freshdeskApiKey);
        System.out.println("\n");

        JsonNode responderNode = fetchResponder();
        System.out.println("\n\tResponder: " + responderNode.toString());
        System.out.println("\n");

        /*
          Go ahead and validate schemas here.
         */

        defaultResponderId = responderNode.get("id").asText();
        JsonNode contactNode = responderNode.get("contact");
        String contactName = contactNode.get("name").asText();
        responderMap.put(defaultResponderId, contactName);

    }

    public void initializeRequester() {

        if (defaultRequesterId.equals("123456")) {
            throw new IllegalStateException("No requester ID provided.");
        }

        System.out.println("\n\tInitializing Requester for ID: " + defaultRequesterId);
        System.out.println("\n");

        JsonNode requester = fetchRequester();
        System.out.println("\n\tRequester: " + requester);
        System.out.println("\n");

        /*
          Go ahead and validate schemas here.
         */

        if (requester == null) {
            throw new IllegalStateException("Requester: "+defaultRequesterId+" not found!");
        }
    }


    public JsonNode fetchResponder() {

        return restClient.get()
                .uri("/agents/me")
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode fetchRequester() {

        return restClient.get()
                .uri("/contacts/"+defaultRequesterId)
                .retrieve()
                .body(JsonNode.class);
    }

    public String getResponderName() {
        return responderMap.get(defaultResponderId);
    }

    public String getResponderId() {
        return defaultResponderId;
    }

    public String getRequesterId() {
        return defaultRequesterId;
    }


}
