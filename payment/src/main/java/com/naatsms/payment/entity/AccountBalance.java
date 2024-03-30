package com.naatsms.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("AccountBalance")
public record AccountBalance(
        @Id Long id,
        @Column("merchant_id") Long merchantId,
        @Column("currency_iso") String currencyIso,
        @Column BigDecimal amount
)
{}
