package com.naatsms.payment.repository.extensions;

import com.naatsms.payment.entity.PaymentTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @deprecated Deprecated due to poor (probably) performance (benchmarks to be performed).
 */
@Deprecated(forRemoval = true)
public interface FindTransactionEnriched<T> {

    Mono<T> findByUuidEnriched(UUID uuid);

    Flux<PaymentTransaction> findByDateRangeForMerchant(Long merchantId, LocalDateTime dateFrom, LocalDateTime dateTo);

}
