package com.naatsms.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("Card")
public record Card(
        @Id Long id,
        @Column("card_number") String cardNumber,
        @Column("exp_date") LocalDate expDate,
        String ccv,
        BigDecimal amount)
{ }