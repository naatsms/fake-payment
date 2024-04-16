package com.naatsms.payment.repository;

import com.naatsms.payment.entity.Card;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface CardRepository extends ReactiveCrudRepository<Card, Long>
{
    Mono<Card> findByCardNumber(String cardNumber);

    @Query("SELECT * FROM card JOIN customer c on card.id = c.card_id where c.id = :customerId FOR UPDATE")
    Mono<Card> selectForUpdateByCustomerId(Long customerId);

    @Query("SELECT * FROM card JOIN customer c on card.id = c.card_id where c.id = :customerId")
    Mono<Card> findByCustomerId(Long customerId);

    @Modifying
    @Query("UPDATE card SET card_amount = card_amount + :amount WHERE id = :cardId")
    Mono<Void> addAmountByCardId(Long cardId, BigDecimal amount);

    @Modifying
    @Query("UPDATE card SET card_amount = card_amount - :amount WHERE id = :cardId")
    Mono<Void> substractAmountByCardId(Long cardId, BigDecimal amount);

}