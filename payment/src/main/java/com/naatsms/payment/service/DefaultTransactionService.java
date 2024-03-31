package com.naatsms.payment.service;

import com.naatsms.payment.dto.CardDto;
import com.naatsms.payment.dto.CustomerDto;
import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.AccountBalance;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.Customer;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.CustomerRepository;
import com.naatsms.payment.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;


@Service
public class DefaultTransactionService implements TransactionService
{

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public DefaultTransactionService(final TransactionRepository transactionRepository, final CardRepository cardRepository, final CustomerRepository customerRepository, final AccountRepository accountRepository)
    {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<PaymentTransaction> createTransaction(final PaymentTransactionDto transactionData, final Long merchantId)
    {
        Mono<Long> customerId = cardRepository.findByCardNumber(transactionData.card().cardNumber())
                .switchIfEmpty(cardRepository.save(toCardEntity(transactionData.card())))
                .flatMap(card -> getCustomerMono(transactionData, card))
                .map(Customer::id);
        Mono<Long> accountBalanceId = accountRepository.findByMerchantIdAndCurrencyIso(merchantId, transactionData.currencyIso())
                .switchIfEmpty(accountRepository.save(toAccountEntity(transactionData, merchantId)))
                .map(AccountBalance::id);
        return Mono.zip(customerId, accountBalanceId)
                .map(tuple -> toTransactionEntity(transactionData, tuple.getT1(), tuple.getT2()))
                .flatMap(transactionRepository::save);
    }

    private AccountBalance toAccountEntity(final PaymentTransactionDto transactionData, final Long merchantId)
    {
        return new AccountBalance(null, merchantId, transactionData.currencyIso(), null);
    }

    private Mono<Customer> getCustomerMono(final PaymentTransactionDto transactionData, final Card card)
    {
        var customer = transactionData.customer();
        return customerRepository.findByFirstNameAndLastNameAndCardId(customer.firstName(), customer.lastName(), card.id())
                          .switchIfEmpty(customerRepository.save(toCustomerEntity(customer, card.id())));
    }

    private PaymentTransaction toTransactionEntity(final PaymentTransactionDto transactionData, Long customerId, Long accountId)
    {
     return new PaymentTransaction(null,
             transactionData.paymentMethod(),
             TransactionType.TRANSACTION,
             TransactionStatus.IN_PROGRESS,
             transactionData.amount(),
             transactionData.createdAt(),
             transactionData.updatedAt(),
             accountId,
             customerId,
             transactionData.languageIso(),
             transactionData.notificationUrl());
    }

    private Card toCardEntity(final CardDto card)
    {
        return new Card(null,
                card.cardNumber(),
                LocalDate.ofInstant(card.expDate().toInstant(), ZoneId.systemDefault()),
                card.ccv(),
                null);
    }

    private Customer toCustomerEntity(final CustomerDto customer, final Long cardId)
    {
        return new Customer(null,
                cardId,
                customer.firstName(),
                customer.lastName(),
                customer.country());
    }

}