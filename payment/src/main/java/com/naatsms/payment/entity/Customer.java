package com.naatsms.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer")
public record Customer(
        @Id Long id,
        @Column("card_id") Long cardId,
        @Column("first_name") String firstName,
        @Column("last_name") String lastName,
        @Column String country)
{}
