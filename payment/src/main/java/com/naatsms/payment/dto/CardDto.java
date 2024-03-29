package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record CardDto(
        @JsonProperty("card_number") String cardNumber,
        @JsonProperty("exp_date") @JsonFormat(pattern = "MM/yy") Date expDate,
        String cvv)
{}