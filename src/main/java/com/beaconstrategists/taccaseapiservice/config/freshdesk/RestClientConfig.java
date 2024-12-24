package com.beaconstrategists.taccaseapiservice.config.freshdesk;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
public class RestClientConfig {

    @Value("${FD_API_KEY:0123456789ABCDEFGHI}")
    private String freshdeskApiKey;

    @Getter
    @Value("${FD_BASE_URI:https://domain.freshdesk.com/api/v2}")
    private String freshdeskBaseUri;

    public RestClientConfig() {
    }

    @Bean
    @Qualifier("fieldPresenceSnakeCaseSerializingRestClient")
    public RestClient fieldPresenceSnakeCaseRestClient(@Qualifier("fieldPresenceSnakeCaseSerializingObjectMapper") ObjectMapper objectMapper) {
        // Create custom JSON message converter
        MappingJackson2HttpMessageConverter messageConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        // Configure converters for RestClient
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(messageConverter);

        // Build and return the RestClient
        return RestClient.builder()
                .baseUrl(freshdeskBaseUri)
                .defaultHeader("Authorization", "Basic " + getBase64ApiKey())
                .messageConverters(converters)
                .requestInterceptor(new LoggingInterceptor())
                .build();
    }

    @Bean
    @Primary
    @Qualifier("snakeCaseRestClient")
    public RestClient snakeCaseRestClient(@Qualifier("snakeCaseObjectMapper") ObjectMapper snakeCaseObjectMapper) {
        // Create custom JSON message converter
        MappingJackson2HttpMessageConverter messageConverter =
                new MappingJackson2HttpMessageConverter(snakeCaseObjectMapper);

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

        // Configure converters for RestClient
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(messageConverter);
        converters.add(formHttpMessageConverter);

        // Build and return the RestClient
        return RestClient.builder()
                .baseUrl(freshdeskBaseUri)
                .defaultHeader("Authorization", "Basic " + getBase64ApiKey())
                .messageConverters(converters)
                .requestInterceptor(new LoggingInterceptor())
                .build();
    }

    @Bean
    @Qualifier("camelCaseRestClient")
    public RestClient camelCaserestClient(@Qualifier("camelCaseObjectMapper") ObjectMapper camelCaseObjectMapper) {
        // Create custom JSON message converter
        MappingJackson2HttpMessageConverter messageConverter =
                new MappingJackson2HttpMessageConverter(camelCaseObjectMapper);

        // Configure converters for RestClient
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(messageConverter);

        // Build and return the RestClient
        return RestClient.builder()
                .baseUrl(freshdeskBaseUri)
                .defaultHeader("Authorization", "Basic " + getBase64ApiKey())
                .messageConverters(converters)
                .requestInterceptor(new LoggingInterceptor())
                .build();
    }

    @Bean
    @Qualifier("simpleRestClient")
    public RestClient simpleRestClient() {

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

        // Configure converters for RestClient
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(formHttpMessageConverter);

        return RestClient.builder()
                .baseUrl(freshdeskBaseUri)
                .defaultHeader("Authorization", "Basic " + getBase64ApiKey())
                .requestInterceptor(new LoggingInterceptor())
                .messageConverters(converters)
                .build();
    }

    private String getBase64ApiKey() {
        return Base64.getEncoder().encodeToString((freshdeskApiKey + ":X").getBytes());
    }

}

