package com.naatsms.payment.service.impl;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.AccountBalance;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.Customer;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.exception.AccountNotFoundException;
import com.naatsms.payment.exception.BusinessException;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.CustomerRepository;
import com.naatsms.payment.repository.TransactionRepository;
import com.naatsms.payment.service.TransactionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

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
    public Mono<PaymentTransaction> createTransaction(final PaymentTransactionDto transactionData, final TransactionType type, final Long merchantId)
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

    @Override
    public Mono<PaymentTransaction> getTransactionDetails(UUID transactionUuid, TransactionType type, Long merchantId) {
        return transactionRepository.findByUuidEnriched(transactionUuid)
                .flatMap(transaction -> verifyType(transaction, type))
                .flatMap(transaction -> verifyAccount(transaction, merchantId));
    }

    private Mono<PaymentTransaction> verifyAccount(PaymentTransaction transaction, Long merchantId) {
        return accountRepository.findByMerchantIdAndCurrencyIso(merchantId, transaction.getCurrencyIso())
                .filter(accountBalance -> accountBalance.id().equals(transaction.getAccountBalanceId()))
                .map(account -> transaction)
                .switchIfEmpty(Mono.error(new BusinessException("Transaction with uuid %d doesn't belong to merchant")));
    }

    private Mono<PaymentTransaction> verifyType(PaymentTransaction transaction, TransactionType type) {
        if (!transaction.getType().equals(type)) {
            throw new BusinessException("No matched transaction for uuid %s and type %s".formatted(transaction.getUuid(), type));
        }
        return Mono.just(transaction);
    }

    @Override
    public Flux<PaymentTransaction> getTransactionsForDateRange(LocalDateTime from, LocalDateTime to, TransactionType type, Long merchantId) {
        if (from == null && to == null) {
            from = LocalDate.now().atStartOfDay();
            to = from.plusDays(1);
        }
        else if (from == null) {
            from = LocalDateTime.MIN;
        }
        else if (to == null) {
            to = LocalDateTime.MAX;
        }

        //TODO implement
        return null;
    }

}