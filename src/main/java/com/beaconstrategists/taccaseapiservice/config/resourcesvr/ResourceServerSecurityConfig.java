package com.beaconstrategists.taccaseapiservice.config.resourcesvr;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerSecurityConfig {

//    @Value("${AUTH_SVC_ISSUER_URI:http://localhost:8080/oauth2/jwks}")
//    private String issuerUri;

    private final JwtDecoder jwtDecoder;

    public ResourceServerSecurityConfig(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean(name = "ProdSecurityFilterChain")
    @Order(1)
    @ConditionalOnProperty(name = "API_SVR_ENV", havingValue = "production", matchIfMissing = false)
    public SecurityFilterChain prodResourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply security specifically for API endpoints
        http.securityMatcher("/api/**") // Match only Resource Server endpoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs*/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/**").authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

        return http.build();
    }

    @Bean(name = "DevSecurityFilterChain")
    @Order(2)
    @ConditionalOnProperty(name = "API_SVR_ENV", havingValue = "development", matchIfMissing = true)
    public SecurityFilterChain devResourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply security specifically for API endpoints
        http.securityMatcher("/api/**") // Match only Resource Server endpoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**","/v3/api-docs/**", "/v3/api-docs*/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/**").permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

        return http.build();
    }

//    //fixme: this may need attention
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withJwkSetUri(issuerUri).build();
//    }
}
