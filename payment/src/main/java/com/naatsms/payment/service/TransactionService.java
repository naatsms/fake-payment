package com.naatsms.payment.service;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.PaymentTransaction;
import reactor.core.publisher.Mono;

public interface TransactionService
{
    Mono<PaymentTransaction> createTransaction(PaymentTransactionDto transactionData, final Long merchant);
}
