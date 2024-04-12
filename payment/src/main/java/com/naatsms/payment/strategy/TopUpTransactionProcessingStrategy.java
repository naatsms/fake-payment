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
        final BigDecimal amount = paymentTransaction.getAmount();
        return cardRepository.selectForUpdateByCustomerId(paymentTransaction.getCustomerId())
                .as(transactionalOperator::transactional)
                .flatMap(card -> validateSufficientBalance(amount, card))
                .flatMap(card -> cardRepository.updateAmountByCardId(card.getId(), card.getAmount().subtract(amount)))
                .then(accountRepository.selectForUpdateById(paymentTransaction.getAccountId()))
                .flatMap(account -> accountRepository.updateAmountByAccountId(account.id(), account.amount().add(amount)))
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.getUuid(), TransactionStatus.SUCCESS, Messages.OK))
                .map(mono -> paymentTransaction);
    }

    private Mono<Card> validateSufficientBalance(final BigDecimal amount, final Card card)
    {
        if (card.getAmount().compareTo(amount) < 0) {
            throw new InsufficientCardBalanceException("Insufficient balance for the card " + card.getCardNumber());
        }
        return Mono.just(card);
    }

}