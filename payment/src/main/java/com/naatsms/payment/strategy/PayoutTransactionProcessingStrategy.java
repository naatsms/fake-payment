package com.naatsms.payment.strategy;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.entity.AccountBalance;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.exception.InsufficientAccountBalanceException;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@Qualifier("payoutProcessingStrategy")
public class PayoutTransactionProcessingStrategy implements TransactionProcessingStrategy
{

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionalOperator transactionalOperator;

    public PayoutTransactionProcessingStrategy(final TransactionRepository transactionRepository, final CardRepository cardRepository, final AccountRepository accountRepository, final TransactionalOperator transactionalOperator)
    {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.transactionalOperator = transactionalOperator;
    }

    public Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction)
    {
        BigDecimal amount = paymentTransaction.amount();
        return cardRepository.selectForUpdateByCustomerId(paymentTransaction.customerId())
                .flatMap(card -> cardRepository.updateAmountByCardId(card.id(), card.amount().add(amount)))
                .then(accountRepository.selectForUpdateById(paymentTransaction.accountBalanceId()))
                .flatMap(accountBalance -> validateSufficientBalance(amount, accountBalance))
                .flatMap(accountBalance -> accountRepository.updateAmountByAccountId(accountBalance.id(), accountBalance.amount().subtract(amount)))
                .then(transactionRepository.selectForUpdateByUuid(paymentTransaction.uuid()))
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.uuid(), TransactionStatus.SUCCESS, Messages.OK))
                .as(transactionalOperator::transactional)
                .map(mono -> paymentTransaction);
    }

    private Mono<AccountBalance> validateSufficientBalance(final BigDecimal amount, final AccountBalance accountBalance)
    {
        if (accountBalance.amount().compareTo(amount) < 0) {
            throw new InsufficientAccountBalanceException("Insufficient balance on account " + accountBalance.merchantId() + " for currency " + accountBalance.currencyIso());
        }
        return Mono.just(accountBalance);
    }

}