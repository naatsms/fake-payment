package com.naatsms.payment.entity;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.enums.PaymentMethod;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("paymentTransaction")
public record PaymentTransaction(
        @Id UUID uuid,
        @Column("payment_method") PaymentMethod paymentMethod,
        @Column TransactionType type,
        @Column TransactionStatus status,
        @Column BigDecimal amount,
        @Column("created_at") LocalDateTime createdAt,
        @Column("updated_at") LocalDateTime updatedAt,
        @Column("account_id") Long accountBalanceId,
        @Column("customer_id") Long customerId,
        @Column("language_iso") String languageIso,
        @Column("notification_url") String notificationUrl
) {

    public static PaymentTransaction fromDto(PaymentTransactionDto transactionDto, Long customerId, Long accountId) {
        return new PaymentTransaction(null,
                transactionDto.paymentMethod(),
                TransactionType.TRANSACTION,
                TransactionStatus.IN_PROGRESS,
                transactionDto.amount(),
                transactionDto.createdAt(),
                transactionDto.updatedAt(),
                accountId,
                customerId,
                transactionDto.languageIso(),
                transactionDto.notificationUrl());
    }

}