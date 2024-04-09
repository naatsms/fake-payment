package com.naatsms.payment.repository;

import com.naatsms.payment.entity.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class FindTransactionEnrichedImpl implements FindTransactionEnriched<PaymentTransaction> {

    @Autowired
    private DatabaseClient client;

    @Override
    public Mono<PaymentTransaction> findByUuidEnriched(UUID uuid) {
        return client.sql("Select * from paymenttransaction as pt " +
                "JOIN customer c on c.id = pt.customer_id " +
                "JOIN card c2 on c2.id = c.card_id WHERE pt.uuid = :uuid" +
                        "")
                .bind("uuid", uuid)
                .fetch()
                .first()
                .single()
                .flatMap(PaymentTransaction::fromRow);
    }
}
