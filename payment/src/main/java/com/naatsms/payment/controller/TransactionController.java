package com.naatsms.payment.controller;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.dto.TransactionResponseDto;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class TransactionController
{
    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(final TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public Mono<TransactionResponseDto> createTransaction(@RequestBody PaymentTransactionDto transaction, ServerWebExchange exchange) {
        LOG.info("{} is being created...", transaction);
        return transactionService.createTransaction(transaction, exchange.getAttribute("merchantId"))
                .map(this::toDto);
    }

    private TransactionResponseDto toDto(final PaymentTransaction paymentTransaction)
    {
        return new TransactionResponseDto(paymentTransaction.uuid().toString(), paymentTransaction.status(), "OK");
    }

    @ExceptionHandler
    private void handleException(Exception e) {
        LOG.error("An error occurred", e);
    }

}
