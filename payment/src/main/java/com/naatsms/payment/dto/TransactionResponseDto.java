package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TransactionResponseDto(
        @JsonProperty("transaction_id") String transactionId,
        String status,
        @JsonProperty("error_code") String errorCode,
        String message
        )
{
}
