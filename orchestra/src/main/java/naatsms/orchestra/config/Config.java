package naatsms.orchestra.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.authorization.client.AuthzClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class Config {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
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
    public AuthzClient authClient(@Value("${keycloak.auth.realm}") String realm,
                                  @Value("${keycloak.baseUrl}") String serverUrl,
                                  @Value("${keycloak.auth.clientId}") String clientId,
                                  @Value("${keycloak.auth.credentials.secret}") String secret){
        var config = new org.keycloak.authorization.client.Configuration(serverUrl, realm, clientId, Map.of("secret", secret), null);
        return AuthzClient.create(config);
    }

    @Bean
    public WebClient webClient(@Value("${application.person.baseUrl}") String personBaseUrl) {
        return WebClient.builder()
                .baseUrl(personBaseUrl)
                .build();
    }

}