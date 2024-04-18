package com.naatsms.payment.service.impl;

import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.exception.TransactionProcessingException;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.service.OperationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.random.RandomGeneratorFactory;

@Service
public class DefaultOperationService implements OperationService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Value("${payment.processing.error.probability:0f}")
    private float errorProbability;

    public DefaultOperationService(CardRepository cardRepository, AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<Void> topUpCardBalance(PaymentTransaction transaction) {
        return Mono.just(transaction)
                .flatMap(emitErrorWithProbability(errorProbability))
                .map(PaymentTransaction::getAmount)
                .zipWith(cardRepository.selectForUpdateByCustomerId(transaction.getCustomerId()))
                .flatMap(tuple -> cardRepository.addAmountByCardId(tuple.getT2().getId(), tuple.getT1()));
    }

    @Override
    public Mono<PaymentTransaction> withdrawCardBalance(PaymentTransaction transaction) {
        return Mono.just(transaction)
                .flatMap(emitErrorWithProbability(errorProbability))
                .map(PaymentTransaction::getAmount)
                .zipWith(cardRepository.selectForUpdateByCustomerId(transaction.getCustomerId()))
                .flatMap(tuple -> cardRepository.substractAmountByCardId(tuple.getT2().getId(), tuple.getT1()))
                .thenReturn(transaction);
    }

    @Override
    public Mono<Void> topUpAccountBalance(PaymentTransaction transaction) {
        return Mono.just(transaction)
                .flatMap(emitErrorWithProbability(errorProbability))
                .map(PaymentTransaction::getAmount)
                .zipWith(accountRepository.selectForUpdateById(transaction.getAccountId()))
                .flatMap(tuple -> accountRepository.addAmountByAccountId(tuple.getT2().getId(), tuple.getT1()));
    }

    @Override
    public Mono<PaymentTransaction> withdrawAccountBalance(PaymentTransaction transaction) {
        return Mono.just(transaction)
                .flatMap(emitErrorWithProbability(errorProbability))
                .map(PaymentTransaction::getAmount)
                .zipWith(accountRepository.selectForUpdateById(transaction.getAccountId()))
                .flatMap(tuple -> accountRepository.addAmountByAccountId(tuple.getT2().getId(), tuple.getT1()))
                .thenReturn(transaction);
    }

    private static Function<PaymentTransaction, Mono<PaymentTransaction>> emitErrorWithProbability(Float errorProbability) {
        return transaction -> {
            if (RandomGeneratorFactory.getDefault().create().nextFloat(100) < errorProbability) {
                return Mono.error(new TransactionProcessingException("Exception occurred"));
            }
            return Mono.just(transaction);
        };
    }


}