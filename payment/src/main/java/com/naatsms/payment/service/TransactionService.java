package com.naatsms.payment.service;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionService
{
    Mono<PaymentTransaction> createTransaction(PaymentTransactionDto transactionData, TransactionType type, final Long merchant);

    Mono<PaymentTransaction> getTransactionDetails(UUID transactionUuid, TransactionType type, final Long merchantId);

    Flux<PaymentTransaction> getTransactionsForDateRange(LocalDateTime from, LocalDateTime to, TransactionType type, Long merchantId);

}
