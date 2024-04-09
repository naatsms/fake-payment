package com.naatsms.payment.entity.callbacks;

import com.naatsms.payment.entity.Card;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class CardBeforeConvertCallback implements BeforeConvertCallback<Card> {

    @Value("${payment.default.card.amount}")
    private BigDecimal defaultAmount;

    @Override
    public Publisher<Card> onBeforeConvert(Card entity, SqlIdentifier table) {
        return Mono.just(entity)
                .filter(card -> entity.getAmount() == null)
                .doOnNext(card -> card.setAmount(defaultAmount))
                .defaultIfEmpty(entity);
    }

}