package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionDetailsDto(
@JsonProperty("transaction_id") String transactionId,
@JsonProperty("payment_method") PaymentMethod paymentMethod,
@JsonProperty("amount") BigDecimal amount,
@JsonProperty("currency") String currency,
@JsonProperty("type") String type,
@JsonProperty("created_at") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") LocalDateTime createdAt,
@JsonProperty("updated_at") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") LocalDateTime updatedAt,
@JsonProperty("card_data") CardDto card,
@JsonProperty("language") String language,
@JsonProperty("customer") CustomerDto customer,
@JsonProperty("status") String status,
@JsonProperty("message") String message,
@JsonProperty("notification_url") String notificationUrl)
{
    public static TransactionDetailsDto fromTransactionEntity(PaymentTransaction transaction) {
        String cardNumberMasked = transaction.getCard().getCardNumber().replaceFirst("(?<=^\\d{4})(\\d)+(?=\\d{4}$)", "***");
        var card = new CardDto(cardNumberMasked, null, null);
        var customer = new CustomerDto(transaction.getCustomer().firstName(), transaction.getCustomer().lastName(), null);
        return new TransactionDetailsDto(
                transaction.getUuid().toString(),
                transaction.getPaymentMethod(),
                transaction.getAmount(),
                transaction.getCurrencyIso(),
                transaction.getType().toString(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt(),
                card,
                transaction.getLanguageIso(),
                customer,
                transaction.getStatus().toString(),
                transaction.getMessage(),
                transaction.getNotificationUrl());
    }

}
