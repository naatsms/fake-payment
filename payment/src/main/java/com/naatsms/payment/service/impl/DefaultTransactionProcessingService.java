package com.naatsms.payment.service.impl;

import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.exception.BusinessException;
import com.naatsms.payment.repository.TransactionRepository;
import com.naatsms.payment.service.TransactionProcessingService;
import com.naatsms.payment.strategy.TransactionProcessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Predicate;

@Service
public class DefaultTransactionProcessingService implements TransactionProcessingService
{
    public static final Logger LOG = LoggerFactory.getLogger(DefaultTransactionProcessingService.class);

    private final TransactionProcessingStrategy topUpProcessingStrategy;
    private final TransactionProcessingStrategy payOutProcessingStrategy;
    private final TransactionRepository transactionRepository;

    private static final Predicate<Throwable> lockFailurePredicate = PessimisticLockingFailureException.class::isInstance;

    public DefaultTransactionProcessingService(
            @Qualifier("topUpProcessingStrategy") final TransactionProcessingStrategy topUpProcessingStrategy,
            @Qualifier("payoutProcessingStrategy") final TransactionProcessingStrategy payOutProcessingStrategy,
            final TransactionRepository transactionRepository)
    {
        this.topUpProcessingStrategy = topUpProcessingStrategy;
        this.payOutProcessingStrategy = payOutProcessingStrategy;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Scheduled(cron = "5/10 * * ? * *")
    public void processTopUpTransactions() {
        transactionRepository.findByStatusAndType(TransactionStatus.IN_PROGRESS, TransactionType.TRANSACTION)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(pt -> LOG.info("Process top-up transaction {}...", pt.uuid()))
                .flatMap(pt -> topUpProcessingStrategy.processTransaction(pt)
                        .onErrorResume(ex -> handleTransactionError(ex, pt)))
                .doOnNext(pt -> LOG.info("Top-up transaction {} has been processed successfully", pt.uuid()))
                .doOnNext(this::sendNotification)
                .subscribe();
    }

    @Override
    @Scheduled(cron = "0/10 * * ? * *")
    public void processPayoutTransactions() {
        transactionRepository.findByStatusAndType(TransactionStatus.IN_PROGRESS, TransactionType.PAYOUT)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(pt -> LOG.info("Process payout transaction {}...", pt.uuid()))
                .flatMap(pt -> payOutProcessingStrategy.processTransaction(pt)
                        .onErrorResume(ex -> handleTransactionError(ex, pt)))
                .doOnNext(pt -> LOG.info("Payout transaction {} has been processed successfully", pt.uuid()))
                .doOnNext(this::sendNotification)
                .subscribe();
    }

    private Mono<PaymentTransaction> handleTransactionError(final Throwable ex, final PaymentTransaction pt)
    {
        if (lockFailurePredicate.test(ex))
        {
            LOG.error("Failed to acquire a lock during processing, next attempt in 10 seconds");
            return Mono.empty();
        }
        LOG.error("Error during processing of transaction {}", pt.uuid());
        if (ex instanceof BusinessException) {
            LOG.error("Processing error: {}", ex.getMessage());
        }
        else {
            LOG.error("Application error: ", ex);
        }
        return transactionRepository.updateStatusByTransactionId(pt.uuid(), TransactionStatus.ERROR, ex.getMessage())
              .thenReturn(pt);
    }

    private void sendNotification(final PaymentTransaction transaction)
    {
        Mono.just("sending notification for transaction " + transaction.uuid())
                .subscribe();
    }

}