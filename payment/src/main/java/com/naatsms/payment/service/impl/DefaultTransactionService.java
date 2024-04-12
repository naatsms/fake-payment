package com.naatsms.payment.service.impl;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.Account;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.Customer;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.entity.projections.IdOnly;
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

import java.time.LocalDateTime;
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
        Mono<Long> customerId = Mono.defer(() -> getOrCreateCard(transactionData))
                .flatMap(card -> getOrCreateCustomer(transactionData, card))
                .map(Customer::id);
        Mono<Long> accountId = accountRepository.findByMerchantIdAndCurrencyIso(merchantId, transactionData.currencyIso())
                .switchIfEmpty(Mono.error(() -> new AccountNotFoundException("Account not found for merchant: " + merchantId + " and currency: " + transactionData.currencyIso())))
                .map(Account::id);
        return Mono.zip(customerId, accountId)
                .map(tuple -> PaymentTransaction.fromDto(transactionData, type, tuple.getT1(), tuple.getT2()))
                .flatMap(transactionRepository::save);
    }

    private Mono<Card> getOrCreateCard(PaymentTransactionDto transactionData) {
        return cardRepository.findByCardNumber(transactionData.card().cardNumber())
                .switchIfEmpty(cardRepository.save(Card.fromDto(transactionData.card())));
    }

    private Mono<Customer> getOrCreateCustomer(final PaymentTransactionDto transactionData, final Card card)
    {
        var customer = transactionData.customer();
        return customerRepository.findByFirstNameAndLastNameAndCardId(customer.firstName(), customer.lastName(), card.getId())
                          .switchIfEmpty(customerRepository.save(Customer.fromDto(customer, card.getId())))
                .contextWrite(context -> context.put("card", card));
    }

    @Override
    public Mono<PaymentTransaction> getTransactionDetails(UUID transactionUuid, TransactionType type, Long merchantId) {
        return transactionRepository.findById(transactionUuid)
                .flatMap(transaction -> verifyType(transaction, type))
                .flatMap(transaction -> verifyAccount(transaction, merchantId))
                .flatMap(this::fetchCustomerAndCardData);
    }

    @Override
    public Mono<PaymentTransaction> getTransactionDetails(UUID transactionUuid) {
        return transactionRepository.findById(transactionUuid)
                .flatMap(this::fetchCustomerAndCardData);
    }

    private Mono<PaymentTransaction> verifyAccount(PaymentTransaction transaction, Long merchantId) {
        return accountRepository.findByMerchantIdAndCurrencyIso(merchantId, transaction.getCurrencyIso())
                .filter(account -> account.id().equals(transaction.getAccountId()))
                .map(account -> transaction)
                .switchIfEmpty(Mono.error(new BusinessException("Transaction with uuid %s doesn't belong to merchant".formatted(transaction.getUuid()))));
    }

    private Mono<PaymentTransaction> verifyType(PaymentTransaction transaction, TransactionType type) {
        if (!transaction.getType().equals(type)) {
            return Mono.error(new BusinessException("No matched transaction for uuid %s and type %s".formatted(transaction.getUuid(), type)));
        }
        return Mono.just(transaction);
    }

    @Override
    public Flux<PaymentTransaction> getTransactionsForDateRange(LocalDateTime from, LocalDateTime to, TransactionType type, Long merchantId) {
        return accountRepository.findAllByMerchantId(merchantId)
                .map(IdOnly::getId)
                .flatMap(accountId -> transactionRepository.findAllByTypeAndAccountIdAndCreatedAtBetween(type, accountId, from, to))
                .flatMap(this::fetchCustomerAndCardData);
    }

    @Override
    public Mono<PaymentTransaction> fetchCustomerAndCardData(PaymentTransaction tr) {
        return customerRepository.findById(tr.getCustomerId())
                .zipWhen(customer -> cardRepository.findById(customer.cardId()))
                .flatMap(tuple -> {
                    tr.setCustomer(tuple.getT1());
                    tr.setCard(tuple.getT2());
                    return Mono.just(tr);
                });
    }

}