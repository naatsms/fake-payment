package com.naatsms.payment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naatsms.payment.dto.TransactionDetailsDto;
import com.naatsms.payment.entity.NotificationLog;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.exception.BusinessException;
import com.naatsms.payment.service.NotificationService;
import com.naatsms.payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Service
@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class DefaultNotificationService implements NotificationService
{

    @Autowired
    private ObjectMapper objectMapper;

    private final TransactionService transactionService;

    public DefaultNotificationService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @Override
    public Mono<Void> sendNotification(final UUID uuid)
    {
        return transactionService.getTransactionDetails(uuid)
                .flatMap(this::sendNotification)
                .then();
    }

    private Mono<Void> sendNotification(final PaymentTransaction transaction)
    {
        return WebClient.create(transaction.getNotificationUrl())
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TransactionDetailsDto.fromTransactionEntity(transaction))
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(10))
                          .filter(WebClientResponseException.class::isInstance))
                .flatMap(entity -> createNotification(entity, transaction))
                .onErrorResume(ExhaustedRetryException.class, ex -> createNotification(ex, transaction))
                .then();
    }

    private Mono<NotificationLog> createNotification(final ResponseEntity<String> entity, final PaymentTransaction transaction)
    {
        return Mono.defer(() -> {
            try
            {
                var request = objectMapper.writeValueAsString(TransactionDetailsDto.fromTransactionEntity(transaction));
                var response = entity.getBody();
                return Mono.just(new NotificationLog(null, transaction.getUuid(), request, response, entity.getStatusCode().value(), transaction.getNotificationUrl()));
            }
            catch (JsonProcessingException e)
            {
                throw new BusinessException(e.getMessage());
            }
        });
    }

    private Mono<NotificationLog> createNotification(final Throwable exception, final PaymentTransaction transaction)
    {
        if (exception.getCause() instanceof WebClientResponseException ex)
        {
            return Mono.defer(() -> {
                try
                {
                    var response = ex.getResponseBodyAs(String.class);
                    var request = objectMapper.writeValueAsString(transaction);
                    return Mono.just(new NotificationLog(null, transaction.getUuid(), request, response, ex.getStatusCode().value(), transaction.getNotificationUrl()));
                }
                catch (JsonProcessingException e)
                {
                    throw new BusinessException(e.getMessage());
                }
            });
        }
        return Mono.empty();
    }

}