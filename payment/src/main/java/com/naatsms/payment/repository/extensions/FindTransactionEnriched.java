package com.naatsms.payment.repository.extensions;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindTransactionEnriched<T> {

    Mono<T> findByUuidEnriched(UUID uuid);

}
