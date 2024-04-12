package com.naatsms.payment.controller;

import com.naatsms.payment.constants.Messages;
import com.naatsms.payment.databind.TimestampToLocalDateEditor;
import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.dto.TransactionDetailsDto;
import com.naatsms.payment.dto.TransactionResponseDto;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.exception.BusinessException;
import com.naatsms.payment.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.naatsms.payment.constants.SessionKeys.MERCHANT_ID;
import static com.naatsms.payment.databind.TimestampToLocalDateEditor.END;
import static com.naatsms.payment.databind.TimestampToLocalDateEditor.START;

@RestController
@RequestMapping("/api/v1/payments")
public class TransactionController
{
    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(final TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, "dateFrom", new TimestampToLocalDateEditor());
        binder.registerCustomEditor(LocalDateTime.class, "dateTo", new TimestampToLocalDateEditor());
    }

    @PostMapping("/topups/")
    public Mono<TransactionResponseDto> createTopUp(@RequestBody PaymentTransactionDto transaction, ServerWebExchange exchange) {
        LOG.info("{} is being created...", transaction);
        return transactionService.createTransaction(transaction, TransactionType.TRANSACTION, exchange.getAttribute(MERCHANT_ID))
                .map(this::toSuccessDto);
    }

    @PostMapping("/payouts/")
    public Mono<TransactionResponseDto> createPayout(@RequestBody PaymentTransactionDto transaction, ServerWebExchange exchange) {
        LOG.info("{} is being created...", transaction);
        return transactionService.createTransaction(transaction, TransactionType.PAYOUT, exchange.getAttribute(MERCHANT_ID))
                .map(this::toSuccessDto);
    }

    @GetMapping("/topups/{uuid}/details")
    public Mono<TransactionDetailsDto> getTopUpDetails(@PathVariable UUID uuid, ServerWebExchange exchange) {
        return transactionService.getTransactionDetails(uuid, TransactionType.TRANSACTION, exchange.getAttribute(MERCHANT_ID))
                .map(this::toDetailsDto);
    }

    @GetMapping("/payouts/{uuid}/details")
    public Mono<TransactionDetailsDto> getPayoutDetails(@PathVariable UUID uuid, ServerWebExchange exchange) {
        return transactionService.getTransactionDetails(uuid, TransactionType.PAYOUT, exchange.getAttribute(MERCHANT_ID))
                .map(this::toDetailsDto);
    }

    @GetMapping("/transactions/list")
    public Flux<TransactionDetailsDto> getTopUpTransactions(@RequestParam(defaultValue = START) LocalDateTime dateFrom, @RequestParam(defaultValue = END) LocalDateTime dateTo, ServerWebExchange exchange) {
        return transactionService.getTransactionsForDateRange(dateFrom, dateTo, TransactionType.TRANSACTION, exchange.getAttribute(MERCHANT_ID))
                .map(this::toDetailsDto);
    }

    @GetMapping("/payouts/list")
    public Flux<TransactionDetailsDto> getPayoutTransactions(@RequestParam(defaultValue = START) LocalDateTime dateFrom, @RequestParam(defaultValue = END) LocalDateTime dateTo, ServerWebExchange exchange) {
        return transactionService.getTransactionsForDateRange(dateFrom, dateTo, TransactionType.PAYOUT, exchange.getAttribute(MERCHANT_ID))
                .map(this::toDetailsDto);
    }

    private TransactionResponseDto toSuccessDto(final PaymentTransaction paymentTransaction)
    {
        return new TransactionResponseDto(paymentTransaction.getUuid().toString(), paymentTransaction.getStatus().toString(), null, "OK");
    }

    private TransactionDetailsDto toDetailsDto(final PaymentTransaction paymentTransaction)
    {
        return TransactionDetailsDto.fromTransactionEntity(paymentTransaction);
    }

    @ExceptionHandler(BusinessException.class)
    public final Mono<ResponseEntity<Object>> handleException(Exception ex, ServerWebExchange exchange) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        if (ex instanceof BusinessException exception) {
            TransactionResponseDto dto = new TransactionResponseDto(null, null, Messages.FAILED, exception.getMessage());
            return Mono.just(new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST));
        }
        return Mono.error(new IllegalStateException());
    }

}
