package com.naatsms.payment.service.impl;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.entity.Account;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.Customer;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.entity.projections.IdOnly;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.exception.AccountNotFoundException;
import com.naatsms.payment.exception.BusinessException;
import com.naatsms.payment.exception.InsufficientAccountBalanceException;
import com.naatsms.payment.exception.InsufficientCardBalanceException;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.CustomerRepository;
import com.naatsms.payment.repository.TransactionRepository;
import com.naatsms.payment.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DefaultTransactionService implements TransactionService
{

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final DefaultOperationService operationService;
    private final TransactionalOperator transactionalOperator;

    public DefaultTransactionService(final TransactionRepository transactionRepository, final CardRepository cardRepository, final CustomerRepository customerRepository, final AccountRepository accountRepository, DefaultOperationService operationService, TransactionalOperator transactionalOperator)
    {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.operationService = operationService;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<PaymentTransaction> createTopUpTransaction(final PaymentTransactionDto transactionData, final Long merchantId)
    {
        return getOrCreateCard(transactionData)
                .flatMap(card -> validateCardBalance(card, transactionData))
                .flatMap(card -> getOrCreateCustomer(transactionData, card))
                .zipWith(getAccountOrError(transactionData, merchantId))
                .map(tuple -> PaymentTransaction.fromDto(transactionData, TransactionType.TRANSACTION, tuple.getT1().id(), tuple.getT2().getId()))
                .flatMap(operationService::withdrawCardBalance)
                .flatMap(transactionRepository::save)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<PaymentTransaction> createPayoutTransaction(final PaymentTransactionDto transactionData, final Long merchantId)
    {
        return getOrCreateCard(transactionData)
                .flatMap(card -> getOrCreateCustomer(transactionData, card))
                .zipWith(getAccountOrError(transactionData, merchantId)
                        .flatMap(account -> validateAccountBalance(account, transactionData)))
                .map(tuple -> PaymentTransaction.fromDto(transactionData, TransactionType.TRANSACTION, tuple.getT1().id(), tuple.getT2().getId()))
                .flatMap(operationService::withdrawAccountBalance)
                .flatMap(transactionRepository::save)
                .as(transactionalOperator::transactional);
    }

    private Mono<Account> getAccountOrError(PaymentTransactionDto transactionData, Long merchantId) {
        return accountRepository.findByMerchantIdAndCurrencyIso(merchantId, transactionData.currencyIso())
                .switchIfEmpty(Mono.error(() -> new AccountNotFoundException("Account not found for merchant: " + merchantId + " and currency: " + transactionData.currencyIso())));
    }

    private Mono<Card> getOrCreateCard(PaymentTransactionDto transactionData) {
        return cardRepository.findByCardNumber(transactionData.card().cardNumber())
                .switchIfEmpty(cardRepository.save(Card.fromDto(transactionData.card())));
    }

    private Mono<Card> validateCardBalance(Card card, PaymentTransactionDto transactionData) {
        if (card.getAmount().compareTo(transactionData.amount()) < 0) {
            return Mono.error(new InsufficientCardBalanceException(Messages.PAYMENT_METHOD_NOT_ALLOWED));
        }
        return Mono.just(card);
    }

    private Mono<Account> validateAccountBalance(Account account, PaymentTransactionDto transactionData) {
        if (account.getAmount().compareTo(transactionData.amount()) < 0) {
            return Mono.error(new InsufficientAccountBalanceException(Messages.PAYOUT_MIN_AMOUNT));
        }
        return Mono.just(account);
    }

    private Mono<Customer> getOrCreateCustomer(final PaymentTransactionDto transactionData, final Card card) {
        var customer = transactionData.customer();
        return customerRepository.findByFirstNameAndLastNameAndCardId(customer.firstName(), customer.lastName(), card.getId())
                          .switchIfEmpty(customerRepository.save(Customer.fromDto(customer, card.getId())));
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
                .filter(account -> account.getId().equals(transaction.getAccountId()))
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