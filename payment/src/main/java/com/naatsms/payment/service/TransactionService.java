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
    Mono<PaymentTransaction> createTopUpTransaction(final PaymentTransactionDto transactionData, final Long merchantId);

    Mono<PaymentTransaction> createPayoutTransaction(final PaymentTransactionDto transactionData, final Long merchantId);

    Mono<PaymentTransaction> getTransactionDetails(UUID transactionUuid, TransactionType type, final Long merchantId);

    Mono<PaymentTransaction> getTransactionDetails(UUID transactionUuid);

    Flux<PaymentTransaction> getTransactionsForDateRange(LocalDateTime from, LocalDateTime to, TransactionType type, Long merchantId);

    Mono<PaymentTransaction> fetchCustomerAndCardData(PaymentTransaction tr);
}
