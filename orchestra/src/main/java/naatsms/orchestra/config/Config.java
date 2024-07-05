package naatsms.orchestra.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

    @Bean
    public Keycloak keycloak(
            @Value("${keycloak.baseUrl}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.clientId}") String clientId,
            @Value("${keycloak.user}") String user,
            @Value("${keycloak.password}") String password) {
        return KeycloakBuilder.builder()
                .username(user)
                .password(password)
                .clientId(clientId)
                .realm(realm)
                .serverUrl(serverUrl).build();
    }

    @Bean
    public WebClient webClient(@Value("${application.person.baseUrl}") String personBaseUrl) {
        return WebClient.builder()
                .baseUrl(personBaseUrl)
                .build();
    }

}