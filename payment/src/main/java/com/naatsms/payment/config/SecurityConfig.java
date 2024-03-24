package com.naatsms.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig
{

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        // @formatter:off
        http
                .authorizeExchange(authorize -> authorize.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        // @formatter:on
        return http.build();
    }

}
