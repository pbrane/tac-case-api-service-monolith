package com.beaconstrategists.taccaseapiservice.config.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class RestControllerConfig {

    @Value("${API_SVR_MAX_FILE_SIZE_MB:20}")
    private Integer maxFileSize;

    @Bean
    @ConfigurationProperties("spring.servlet.multipart")
    public MultipartProperties multipartProperties() {
        MultipartProperties properties = new MultipartProperties();
        properties.setMaxFileSize(DataSize.ofMegabytes(maxFileSize)); // Set max file size to 10MB
        return properties;
    }
}