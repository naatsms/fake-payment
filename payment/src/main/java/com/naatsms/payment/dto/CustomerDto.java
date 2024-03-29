package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerDto(
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String country)
{}
