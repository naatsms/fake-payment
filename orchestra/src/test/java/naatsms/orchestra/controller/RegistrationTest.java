package naatsms.orchestra.controller;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import naatsms.orchestra.constants.dto.AuthenticationResponse;
import naatsms.orchestra.constants.dto.RegistrationRequest;
import naatsms.orchestra.helper.PersonHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
class RegistrationTest {

    private static final Network network = Network.newNetwork();

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("/test-realm.json")
            .withNetwork(network)
            .withNetworkAliases("keycloak");

    @Container
    private static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network);

    private static Proxy keycloakProxy;

    @Autowired
    private WebTestClient webTestClient;

    private static final MockWebServer personServer = new MockWebServer();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {
        var toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
        keycloakProxy = toxiproxyClient.createProxy("keycloakProxy", "0.0.0.0:8666", "keycloak:8080");
        registry.add("application.person.baseUrl", () -> personServer.url("/").url().toString());
        registry.add("keycloak.baseUrl", () -> "http://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(8666));
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(8666) + "/realms/payment");
    }

    @BeforeEach
    void setup() {
        if (!keycloak.isRunning()) {
            keycloak.start();
        }
    }

    @Test
    void testRegistrationPasswordMismatch() throws IOException {
        webTestClient.post().uri("/v1/auth/registration").bodyValue(new RegistrationRequest("testUserName", "testPassword", "testPasswordMistype"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(this::print)
                .jsonPath("$.message").isEqualTo("Password confirmation failed");
    }

    @Test
    @Disabled("not implemented")
    void testRegistrationPersonServiceUnavailable() {
        //TODO https://github.com/naatsms/fake-payment/issues/1
        assertTrue(true);
    }

    @Test
    void testRegistrationSuccess() {
        var uuid = UUID.randomUUID();
        personServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(201)
                .setBody(PersonHelper.getMockResponseBody(uuid, "testUserName@test.com", "testPassword")));

        var token = webTestClient.post().uri("/v1/auth/registration").bodyValue(new RegistrationRequest("testUserName@test.com", "testPassword", "testPassword"))
                .exchange()
                .expectStatus().isCreated().returnResult(AuthenticationResponse.class).getResponseBody().blockFirst();

        personServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(PersonHelper.getMockResponseBody(uuid, "testUserName@test.com", "testPassword")));

        var dto = webTestClient.get()
                .uri("/v1/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.profileId").isEqualTo(uuid.toString());
    }

    @Test
    void testRegistrationFailed_WrongUserName() {
        var uuid = UUID.randomUUID();
        personServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(201)
                .setBody(PersonHelper.getMockResponseBody(uuid, "testUserName", "testPassword")));

        personServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(PersonHelper.getMockResponseBody(uuid, "testUserName", "testPassword")));

        webTestClient.post().uri("/v1/auth/registration").bodyValue(new RegistrationRequest("testUserName@test.com", "testPassword", "testPassword"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(this::print)
                .jsonPath("$.response.errorMessage").isEqualTo("error-invalid-email")
                .jsonPath("$.title").isEqualTo("Internal Server Error");
    }

    @Test
    void testRegistrationFailed_KeycloakNotAvailable() throws IOException {
        keycloakProxy.toxics().timeout("timeout", ToxicDirection.DOWNSTREAM, 10_000);
        var uuid = UUID.randomUUID();
        personServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(201)
                .setBody(PersonHelper.getMockResponseBody(uuid, "testUserName@test.com", "testPassword")));

        personServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(PersonHelper.getMockResponseBody(uuid, "testUserName@test.com", "testPassword")));

        webTestClient.mutate().responseTimeout(Duration.of(15, ChronoUnit.SECONDS)).build().post().uri("/v1/auth/registration")
                .bodyValue(new RegistrationRequest("testUserName@test.com", "testPassword", "testPassword"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(this::print)
                .jsonPath("$.detail").value(val -> assertTrue(val.contains("failed to respond")), String.class)
                .jsonPath("$.title").isEqualTo("Internal Server Error");
    }

    private void print(EntityExchangeResult<byte[]> result) {
        System.out.println(result.toString());
    }

}
