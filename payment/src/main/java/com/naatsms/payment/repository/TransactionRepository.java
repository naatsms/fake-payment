package com.naatsms.payment.repository;

import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<PaymentTransaction, UUID>
{

    @Query("SELECT * FROM paymenttransaction where uuid = :uuid FOR UPDATE")
    Mono<PaymentTransaction> selectForUpdateByUuid(UUID uuid);

    @Modifying
    @Query("UPDATE paymenttransaction SET status = :status, message = :message WHERE uuid = :transactionId")
    Mono<Long> updateStatusByTransactionId(UUID transactionId, TransactionStatus status, String message);

    Flux<PaymentTransaction> findByStatusAndType(TransactionStatus status, TransactionType type);

    Flux<PaymentTransaction> findAllByTypeAndAccountIdAndCreatedAtBetween(TransactionType status, Long accountId, LocalDateTime createdAt, LocalDateTime createdAt2);

}
