package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.naatsms.payment.enums.TransactionStatus;

public record TransactionResponseDto(
        @JsonProperty("transaction_id") String transactionId,
        TransactionStatus status,
        String message
        )
{
}
