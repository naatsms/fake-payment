package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.naatsms.payment.enums.PaymentMethod;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentTransactionDto(
        @JsonProperty("payment_method") PaymentMethod paymentMethod,
        @JsonProperty BigDecimal amount,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        @JsonProperty("currency") String currencyIso,
        @JsonProperty("language") String languageIso,
        @JsonProperty("notification_url") String notificationUrl,
        @JsonProperty("card_data") @Transient CardDto card,
        @JsonProperty("customer") @Transient CustomerDto customer
){}