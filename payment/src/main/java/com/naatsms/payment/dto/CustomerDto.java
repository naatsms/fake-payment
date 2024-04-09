package com.naatsms.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerDto(
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String country)
{}
