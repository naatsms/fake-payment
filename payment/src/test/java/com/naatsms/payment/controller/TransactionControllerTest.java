package com.naatsms.payment.controller;

import com.jayway.jsonpath.JsonPath;
import com.naatsms.payment.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
@AutoConfigureWebTestClient
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testCreateTransaction()
    {
        // Given
        String requestJson = "{\"payment_method\":\"CARD\",\"amount\":\"1000\",\"currency\":\"BRL\",\"created_at\":\"2023-02-16T09:12:34.413\",\"updated_at\":\"2023-02-16T09:12:34.413\",\"card_data\":{\"card_number\":\"4102778822334893\",\"exp_date\":\"11/23\",\"cvv\":\"566\"},\"language\":\"en\",\"notification_url\":\"https://proselyte.net/webhook/transaction\",\"customer\":{\"first_name\":\"John\",\"last_name\":\"Doe\",\"country\":\"BR\"}}";

        // When/Then
        webTestClient.post().uri("/transaction")
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
                    // Extract the transaction ID from the response
                    var jsonPath = JsonPath.compile("$.transaction_id");
                    String transactionId = jsonPath.read(new String(Objects.requireNonNull(response.getResponseBody())));
                    System.out.println(transactionId);
                    // Verify that the transaction with the provided ID exists in the database
                    StepVerifier.create(transactionRepository.findById((UUID.fromString(transactionId))))
                            .expectNextMatches(transaction -> transaction.uuid().toString().equals(transactionId))
                            .verifyComplete();
                });
    }

}