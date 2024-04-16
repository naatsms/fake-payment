package com.naatsms.payment.strategy;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@Qualifier("topUpProcessingStrategy")
public class TopUpTransactionProcessingStrategy implements TransactionProcessingStrategy
{

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionalOperator transactionalOperator;

    public TopUpTransactionProcessingStrategy(final TransactionRepository transactionRepository, final AccountRepository accountRepository, final TransactionalOperator transactionalOperator)
    {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionalOperator = transactionalOperator;
    }

    public Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction)
    {
        final BigDecimal amount = paymentTransaction.getAmount();
        return Mono.defer(() -> accountRepository.selectForUpdateById(paymentTransaction.getAccountId()))
                .flatMap(account -> accountRepository.addAmountByAccountId(account.getId(), amount))
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.getUuid(), TransactionStatus.SUCCESS, Messages.OK))
                .as(transactionalOperator::transactional)
                .then(Mono.just(paymentTransaction));
    }

}