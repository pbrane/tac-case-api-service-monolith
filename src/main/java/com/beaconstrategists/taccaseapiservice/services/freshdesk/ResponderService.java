package com.beaconstrategists.taccaseapiservice.services.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ResponderService {

    private final RestClient restClient;

    @Value("${FD_DEFAULT_RESPONDER_ID:123456}")
    private String defaultResponderId;

    public ResponderService(@Qualifier("camelCaseRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void initializeResponder() {

        if (defaultResponderId.equals("123456")) {
            throw new IllegalStateException("No responder ID provided.");
        }

        System.out.println("\n\tInitializing Responder for ID: " + defaultResponderId);
        System.out.println("\n");

        JsonNode responder = fetchResponder();
        System.out.println("\n\tResponder: " + responder);
        System.out.println("\n");

        /*
          Go ahead and validate schemas here.
         */

        if (responder == null) {
            throw new IllegalStateException("Responder: "+defaultResponderId+" not found!");
        }
    }

    public JsonNode fetchResponder() {

        return restClient.get()
                .uri("/agents/"+defaultResponderId)
                .retrieve()
                .body(JsonNode.class);
    }

    public String getResponderId() {
        return defaultResponderId;
    }
}
