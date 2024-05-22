package naatsms.person.controller;

import naatsms.person.dto.AddressDto;
import naatsms.person.dto.IndividualDto;
import naatsms.person.dto.ProfileDto;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
class IndividualsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:" + postgres.getFirstMappedPort() + "/test");
        registry.add("spring.flyway.url", () -> "jdbc:postgresql://localhost:" + postgres.getFirstMappedPort() + "/test");
    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.migrate();
    }

    @AfterEach
    void tearDown() {
        flyway.clean();
    }

    @Test
    void testCreateAndGetIndividual() {
        AddressDto address = new AddressDto(null, 1, "testAddress", "00000", "Berlin", "Berlin", null, null, null);
        ProfileDto profile = new ProfileDto(null, "ABCD1234", "Jane", "Doe", null, false, address, null, null, null, null);
        IndividualDto individualDto = new IndividualDto(null, "987654321", "987-654-3210", "jane.doe@example.com", profile);

        IndividualDto response = webTestClient.post().uri("/api/individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(IndividualDto.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(response);

        webTestClient.get().uri("/api/individuals/" + response.profileId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.profile.firstName").isEqualTo("Jane")
                .jsonPath("$.profile.lastName").isEqualTo("Doe")
                .jsonPath("$.profile.status").isEqualTo("ACTIVE")
                .jsonPath("$.profile.address.address").isEqualTo("testAddress")
                .jsonPath("$.passportNumber").isEqualTo("987654321");
    }

    @Test
    void testUpdateIndividual() {
        var client = webTestClient.mutate().responseTimeout(Duration.ofHours(1)).build();

        AddressDto address = new AddressDto(null, 1, "testAddress", "00000", "Berlin", "Berlin", null, null, null);
        ProfileDto profile = new ProfileDto(null, "ABCD1234", "Test", "Test", null, false, address, null, null, null, null);
        IndividualDto individualDto = new IndividualDto(null, "987654321", "987-654-3210", "test@example.com", profile);

        IndividualDto responseBody = client.post().uri("/api/individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(IndividualDto.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(responseBody);

        var uuid = responseBody.profileId();

        //AddressDto address2 = new AddressDto(null, null, "testAddress", "00001", null, null, null, null, null);
        ProfileDto profile2 = new ProfileDto(null, "ABCD123456", null, null, null, false, null, null, null, null, null);
        IndividualDto individualDto2 = new IndividualDto(null, null, null, "newexample@test.com", profile2);

        client.put().uri("/api/individuals/" + uuid)
                        .bodyValue(individualDto2)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.profile.secretKey").isEqualTo("ABCD123456");


        client.get().uri("/api/individuals/" + uuid)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.profile.firstName").isEqualTo("Test")
                .jsonPath("$.profile.lastName").isEqualTo("Test")
                .jsonPath("$.profile.secretKey").isEqualTo("ABCD123456")
                .jsonPath("$.profile.status").isEqualTo("ACTIVE")
                .jsonPath("$.profile.address.address").isEqualTo("testAddress")
                .jsonPath("$.passportNumber").isEqualTo("987654321");

        var history = client.get().uri("/api/profile-history/" + uuid)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].profileId").isEqualTo(uuid.toString())
                .jsonPath("$[0].profileType").isEqualTo("INDIVIDUAL")
                .jsonPath("$[0].changedValues.secretKey").isEqualTo("ABCD1234")
                .jsonPath("$[0].changedValues.EMAIL").isEqualTo("test@example.com")
                .jsonPath("$[1].changedValues.secretKey").isEqualTo("ABCD123456")
                .jsonPath("$[1].changedValues.EMAIL").isEqualTo("newexample@test.com");
    }



}