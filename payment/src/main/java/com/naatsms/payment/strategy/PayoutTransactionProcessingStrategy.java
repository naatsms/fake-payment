package com.naatsms.payment.strategy;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.repository.TransactionRepository;
import com.naatsms.payment.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@Qualifier("payoutProcessingStrategy")
@Slf4j
public class PayoutTransactionProcessingStrategy implements TransactionProcessingStrategy
{

    private final TransactionRepository transactionRepository;
    private final OperationService operationService;
    private final TransactionalOperator transactionalOperator;

    public PayoutTransactionProcessingStrategy(final TransactionRepository transactionRepository, OperationService operationService, final TransactionalOperator transactionalOperator)
    {
        this.transactionRepository = transactionRepository;
        this.operationService = operationService;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction)
    {
        return Mono.just(paymentTransaction)
                .flatMap(operationService::topUpCardBalance)
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.getUuid(), TransactionStatus.SUCCESS, Messages.OK))
                .as(transactionalOperator::transactional)
                .thenReturn(paymentTransaction)
                .doOnNext(tr -> log.info("Top-up transaction {} has been processed successfully", tr.getUuid()))
                .onErrorResume(ex -> handleTransactionError(ex, paymentTransaction));
    }

    private Mono<PaymentTransaction> handleTransactionError(final Throwable ex, final PaymentTransaction pt) {
        return Mono.just(pt)
                .doOnError(lockFailurePredicate, e -> log.error("Failed to acquire a lock during processing, postpone...", e))
                .onErrorResume(lockFailurePredicate, e -> Mono.empty())
                .doOnNext(tr -> log.error("Error during processing of transaction {}", tr.getUuid()))
                .doOnNext(tr -> log.error("Application error: ", ex))
                .flatMap(operationService::topUpAccountBalance)
                .then(transactionRepository.updateStatusByTransactionId(pt.getUuid(), TransactionStatus.ERROR, ex.getMessage()))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to release the amount held after transaction processing error, will try to process later...", e))
                .thenReturn(pt);
    }

}