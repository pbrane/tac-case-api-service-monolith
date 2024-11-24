package com.beaconstrategists.taccaseapiservice.config.authsvr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${PG_DATASOURCE_URL:jdbc:postgresql://localhost:5432/tacauthdb}")
    private String datasourceUrl;

    @Value("${PG_DATASOURCE_USERNAME:tacauthuser}")
    private String datasourceUsername;

    @Value("${PR_DATASOURCE_PASSWORD:tacauthpass}")
    private String datasourcePassword;

    @Bean
    @ConditionalOnProperty(name = "AUTH_SVR_REPO", havingValue = "postgres", matchIfMissing = false)
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        return dataSource;
    }
}