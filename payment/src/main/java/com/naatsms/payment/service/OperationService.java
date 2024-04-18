package com.naatsms.payment.service;

import com.naatsms.payment.entity.PaymentTransaction;
import reactor.core.publisher.Mono;

public interface OperationService {

    Mono<Void> topUpCardBalance(PaymentTransaction transaction);

    Mono<PaymentTransaction> withdrawCardBalance(PaymentTransaction transaction);

    Mono<Void> topUpAccountBalance(PaymentTransaction transaction);

    Mono<PaymentTransaction> withdrawAccountBalance(PaymentTransaction transaction);

}
