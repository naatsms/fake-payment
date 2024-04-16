package com.naatsms.payment.controller;

import com.jayway.jsonpath.JsonPath;
import com.naatsms.payment.entity.Account;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.TransactionRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
class TransactionControllerTest {

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
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        flyway.migrate();
    }

    @AfterEach
    void tearDown() {
        flyway.clean();
    }

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testCreateTopupSuccess()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"1000\",\"currency\":\"USD\",\"card_data\":{\"card_number\":\"4102778822334893\",\"exp_date\":\"11/23\",\"ccv\":\"566\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";

        // When/Then
        webTestClient.post().uri("/api/v1/payments/topups/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("name","secret", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.message").isEqualTo("OK")
                .consumeWith(response -> {
                    var jsonPath = JsonPath.compile("$.transaction_id");
                    String transactionId = jsonPath.read(new String(Objects.requireNonNull(response.getResponseBody())));
                    System.out.println(transactionId);
                    StepVerifier.create(transactionRepository.findById((UUID.fromString(transactionId))))
                            .expectNextMatches(transaction -> transaction.getUuid().toString().equals(transactionId))
                            .verifyComplete();
                    StepVerifier.create(transactionRepository.findById((UUID.fromString(transactionId)))
                            .flatMap(tr -> cardRepository.findByCustomerId(tr.getCustomerId()))
                            .map(Card::getAmount))
                            .expectNextMatches(value -> value.compareTo(BigDecimal.ZERO) == 0)
                            .verifyComplete();
                });
    }

    @Test
    void testCreateTopupInsufficcientBalance()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"10000\",\"currency\":\"USD\",\"card_data\":{\"card_number\":\"4102778822334893\",\"exp_date\":\"11/23\",\"cvv\":\"566\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";
        // When/Then
        webTestClient.post().uri("/api/v1/payments/topups/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("name","secret", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error_code").isEqualTo("FAILED")
                .jsonPath("$.message").isEqualTo("PAYMENT_METHOD_NOT_ALLOWED");
    }

    @Test
    void testCreateTopupAccountNotExists()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"100\",\"currency\":\"PLN\",\"card_data\":{\"card_number\":\"4102778822334893\",\"exp_date\":\"11/23\",\"cvv\":\"566\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";
        // When/Then
        webTestClient.post().uri("/api/v1/payments/topups/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("name","secret", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error_code").isEqualTo("FAILED")
                .jsonPath("$.message").isEqualTo("Account not found for merchant: 1 and currency: PLN");
    }

    @Test
    void testCreatePayoutSuccess()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"1000\",\"currency\":\"USD\",\"card_data\":{\"card_number\":\"4102778822334893\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";

        // When/Then
        webTestClient.post().uri("/api/v1/payments/payouts/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("name","secret", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.message").isEqualTo("OK")
                .consumeWith(response -> {
                    var jsonPath = JsonPath.compile("$.transaction_id");
                    String transactionId = jsonPath.read(new String(Objects.requireNonNull(response.getResponseBody())));
                    System.out.println(transactionId);
                    StepVerifier.create(transactionRepository.findById((UUID.fromString(transactionId))))
                            .expectNextMatches(transaction -> transaction.getUuid().toString().equals(transactionId))
                            .verifyComplete();
                    StepVerifier.create(transactionRepository.findById((UUID.fromString(transactionId)))
                                    .flatMap(tr -> accountRepository.findById(tr.getAccountId()))
                                    .map(Account::getAmount))
                            .expectNextMatches(value -> BigDecimal.valueOf(9000).compareTo(value) == 0)
                            .verifyComplete();
                });
    }

    @Test
    void testCreatePayoutInsufficientBalance()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"10001\",\"currency\":\"USD\",\"card_data\":{\"card_number\":\"4102778822334893\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";// When/Then


        webTestClient.post().uri("/api/v1/payments/payouts/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("name","secret", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error_code").isEqualTo("FAILED")
                .jsonPath("$.message").isEqualTo("PAYOUT_MIN_AMOUNT");
    }

    @Test
    void testCreatePayoutAccountNotExists()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"1000\",\"currency\":\"BRL\",\"card_data\":{\"card_number\":\"4102778822334893\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";// When/Then

        // When/Then
        webTestClient.post().uri("/api/v1/payments/payouts/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("name","secret", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error_code").isEqualTo("FAILED")
                .jsonPath("$.message").isEqualTo("Account not found for merchant: 1 and currency: BRL");
    }

}