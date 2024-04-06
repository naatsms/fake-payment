package com.naatsms.payment.strategy;

import com.naatsms.payment.entity.PaymentTransaction;
import reactor.core.publisher.Mono;

public interface TransactionProcessingStrategy
{
    Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction);
}
