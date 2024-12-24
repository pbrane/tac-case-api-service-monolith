package com.beaconstrategists.taccaseapiservice.config.freshdesk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class FreshdeskClientSecurityConfig {

    @Bean
    public SecurityFilterChain controllerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**").authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
