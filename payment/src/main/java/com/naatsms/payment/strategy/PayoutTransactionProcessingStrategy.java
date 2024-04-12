package com.naatsms.payment.strategy;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.entity.Account;
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
        BigDecimal amount = paymentTransaction.getAmount();
        return cardRepository.selectForUpdateByCustomerId(paymentTransaction.getCustomerId())
                .flatMap(card -> cardRepository.updateAmountByCardId(card.getId(), card.getAmount().add(amount)))
                .then(accountRepository.selectForUpdateById(paymentTransaction.getAccountId()))
                .flatMap(account -> validateSufficientBalance(amount, account))
                .flatMap(account -> accountRepository.updateAmountByAccountId(account.id(), account.amount().subtract(amount)))
                .then(transactionRepository.selectForUpdateByUuid(paymentTransaction.getUuid()))
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.getUuid(), TransactionStatus.SUCCESS, Messages.OK))
                .as(transactionalOperator::transactional)
                .map(mono -> paymentTransaction);
    }

    private Mono<Account> validateSufficientBalance(final BigDecimal amount, final Account account)
    {
        if (account.amount().compareTo(amount) < 0) {
            throw new InsufficientAccountBalanceException("Insufficient balance on account " + account.merchantId() + " for currency " + account.currencyIso());
        }
        return Mono.just(account);
    }

}