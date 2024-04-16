package com.naatsms.payment.service.impl;

import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.repository.TransactionRepository;
import com.naatsms.payment.service.NotificationService;
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
public class DefaultTransactionProcessingService implements TransactionProcessingService {
    public static final Logger LOG = LoggerFactory.getLogger(DefaultTransactionProcessingService.class);

    private final TransactionProcessingStrategy topUpProcessingStrategy;
    private final TransactionProcessingStrategy payOutProcessingStrategy;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    private static final Predicate<Throwable> lockFailurePredicate = PessimisticLockingFailureException.class::isInstance;

    public DefaultTransactionProcessingService(
            @Qualifier("topUpProcessingStrategy") final TransactionProcessingStrategy topUpProcessingStrategy,
            @Qualifier("payoutProcessingStrategy") final TransactionProcessingStrategy payOutProcessingStrategy,
            final TransactionRepository transactionRepository,
            final NotificationService notificationService) {
        this.topUpProcessingStrategy = topUpProcessingStrategy;
        this.payOutProcessingStrategy = payOutProcessingStrategy;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Scheduled(cron = "2/4 * * ? * *")
    public void processTopUpTransactions() {
        LOG.info("Processing top up transactions, fetching transactions for processing...");
        transactionRepository.findByStatusAndType(TransactionStatus.IN_PROGRESS, TransactionType.TRANSACTION)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(pt -> LOG.info("Process top-up transaction {}...", pt.getUuid()))
                .flatMap(pt -> topUpProcessingStrategy.processTransaction(pt)
                        .doOnNext(tr -> LOG.info("Top-up transaction {} has been processed successfully", pt.getUuid()))
                        .onErrorResume(ex -> handleTransactionError(ex, pt)))
                .doOnNext(this::sendNotification)
                .subscribe();
    }

    @Override
    @Scheduled(cron = "0/4 * * ? * *")
    public void processPayoutTransactions() {
        LOG.info("Processing payout transactions, fetching transactions for processing...");
        transactionRepository.findByStatusAndType(TransactionStatus.IN_PROGRESS, TransactionType.PAYOUT)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(pt -> LOG.info("Process payout transaction {}...", pt.getUuid()))
                .flatMap(pt -> payOutProcessingStrategy.processTransaction(pt)
                        .doOnNext(tr -> LOG.info("Payout transaction {} has been processed successfully", pt.getUuid()))
                        .onErrorResume(ex -> handleTransactionError(ex, pt)))
                .doOnNext(this::sendNotification)
                .subscribe();
    }

    private Mono<PaymentTransaction> handleTransactionError(final Throwable ex, final PaymentTransaction pt) {
        if (lockFailurePredicate.test(ex)) {
            LOG.error("Failed to acquire a lock during processing, next attempt in 10 seconds", ex);
            return Mono.empty();
        }
        LOG.error("Error during processing of transaction {}", pt.getUuid());
        LOG.error("Application error: ", ex);
        return transactionRepository.updateStatusByTransactionId(pt.getUuid(), TransactionStatus.ERROR, ex.getMessage())
                .thenReturn(pt);
    }

    private void sendNotification(final PaymentTransaction transaction) {
        notificationService.sendNotification(transaction.getUuid()).subscribe();
    }

}