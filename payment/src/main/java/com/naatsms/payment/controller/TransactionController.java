package com.naatsms.payment.controller;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.dto.TransactionResponseDto;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.naatsms.payment.constants.SessionKeys.MERCHANT_ID;

@RestController
@RequestMapping("/api/v1/payments")
public class TransactionController
{
    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(final TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @PostMapping("/topups/")
    public Mono<TransactionResponseDto> createTopUp(@RequestBody PaymentTransactionDto transaction, ServerWebExchange exchange) {
        LOG.info("{} is being created...", transaction);
        return transactionService.createTransaction(transaction, TransactionType.TRANSACTION, exchange.getAttribute(MERCHANT_ID))
                .map(this::toDto);
    }

    @PostMapping("/payouts/")
    public Mono<TransactionResponseDto> createPayout(@RequestBody PaymentTransactionDto transaction, ServerWebExchange exchange) {
        LOG.info("{} is being created...", transaction);
        return transactionService.createTransaction(transaction, TransactionType.PAYOUT, exchange.getAttribute(MERCHANT_ID))
                .map(this::toDto);
    }

    private TransactionResponseDto toDto(final PaymentTransaction paymentTransaction)
    {
        return new TransactionResponseDto(paymentTransaction.getUuid().toString(), paymentTransaction.getStatus(), "OK");
    }

    @ExceptionHandler
    private void handleException(Exception e) {
        LOG.error("An error occurred", e);
    }

}
