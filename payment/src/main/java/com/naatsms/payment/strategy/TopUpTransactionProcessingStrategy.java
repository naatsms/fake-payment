package com.naatsms.payment.strategy;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.exception.InsufficientCardBalanceException;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
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
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionalOperator transactionalOperator;

    public TopUpTransactionProcessingStrategy(final TransactionRepository transactionRepository, final CardRepository cardRepository, final AccountRepository accountRepository, final TransactionalOperator transactionalOperator)
    {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.transactionalOperator = transactionalOperator;
    }

    public Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction)
    {
        final BigDecimal amount = paymentTransaction.amount();
        return cardRepository.selectForUpdateByCustomerId(paymentTransaction.customerId())
                .as(transactionalOperator::transactional)
                .flatMap(card -> validateSufficientBalance(amount, card))
                .flatMap(card -> cardRepository.updateAmountByCardId(card.id(), card.amount().subtract(amount)))
                .then(accountRepository.selectForUpdateById(paymentTransaction.accountBalanceId()))
                .flatMap(accountBalance -> accountRepository.updateAmountByAccountId(accountBalance.id(), accountBalance.amount().add(amount)))
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.uuid(), TransactionStatus.SUCCESS, Messages.OK))
                .log()
                .map(mono -> paymentTransaction);
    }

    private Mono<Card> validateSufficientBalance(final BigDecimal amount, final Card card)
    {
        if (card.amount().compareTo(amount) < 0) {
            throw new InsufficientCardBalanceException();
        }
        return Mono.just(card);
    }

}