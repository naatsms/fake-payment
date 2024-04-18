package com.naatsms.payment.strategy;

import com.naatsms.payment.entity.PaymentTransaction;
import org.springframework.dao.PessimisticLockingFailureException;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public interface TransactionProcessingStrategy
{
    Predicate<Throwable> lockFailurePredicate = PessimisticLockingFailureException.class::isInstance;

    Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction);

    default Mono<PaymentTransaction> releaseHold(final PaymentTransaction paymentTransaction) {
        return Mono.empty();
    }

}