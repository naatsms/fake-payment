package com.naatsms.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("merchant")
public record Merchant(
        @Id Long id,
        @Column String name,
        @Column String secret
){}