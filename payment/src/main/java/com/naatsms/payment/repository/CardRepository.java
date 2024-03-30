package com.naatsms.payment.repository;

import com.naatsms.payment.entity.Card;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CardRepository extends ReactiveCrudRepository<Card, String>
{
    Mono<Card> findByCardNumber(String cardNumber);
}
