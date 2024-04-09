package com.naatsms.payment.service.impl;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.AccountBalance;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.Customer;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.exception.AccountNotFoundException;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.CustomerRepository;
import com.naatsms.payment.repository.TransactionRepository;
import com.naatsms.payment.service.TransactionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * TODO Balance validation before save
 */

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
                .switchIfEmpty(cardRepository.save(Card.fromDto(transactionData.card())))
                .flatMap(card -> getCustomerMono(transactionData, card))
                .map(Customer::id);
        Mono<Long> accountBalanceId = accountRepository.findByMerchantIdAndCurrencyIso(merchantId, transactionData.currencyIso())
                .switchIfEmpty(Mono.error(() -> new AccountNotFoundException("Account not found for merchant: " + merchantId + "and currency: " + transactionData.currencyIso())))
                .map(AccountBalance::id);
        return Mono.zip(customerId, accountBalanceId)
                .map(tuple -> PaymentTransaction.fromDto(transactionData, type, tuple.getT1(), tuple.getT2()))
                .flatMap(transactionRepository::save);
    }

    private Mono<Customer> getCustomerMono(final PaymentTransactionDto transactionData, final Card card)
    {
        var customer = transactionData.customer();
        return customerRepository.findByFirstNameAndLastNameAndCardId(customer.firstName(), customer.lastName(), card.getId())
                          .switchIfEmpty(customerRepository.save(Customer.fromDto(customer, card.getId())));
    }

}