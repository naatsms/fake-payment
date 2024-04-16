package com.naatsms.payment.strategy;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
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
    private final TransactionalOperator transactionalOperator;

    public PayoutTransactionProcessingStrategy(final TransactionRepository transactionRepository, final CardRepository cardRepository, final TransactionalOperator transactionalOperator)
    {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<PaymentTransaction> processTransaction(final PaymentTransaction paymentTransaction)
    {
        BigDecimal amount = paymentTransaction.getAmount();
        return Mono.defer(() -> cardRepository.selectForUpdateByCustomerId(paymentTransaction.getCustomerId()))
                .flatMap(card -> cardRepository.addAmountByCardId(card.getId(), amount))
                .then(transactionRepository.updateStatusByTransactionId(paymentTransaction.getUuid(), TransactionStatus.SUCCESS, Messages.OK))
                .as(transactionalOperator::transactional)
                .then(Mono.just(paymentTransaction));
    }

}