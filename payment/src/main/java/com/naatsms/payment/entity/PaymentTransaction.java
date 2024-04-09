package com.naatsms.payment.entity;

import com.naatsms.payment.dto.PaymentTransactionDto;
import com.naatsms.payment.enums.PaymentMethod;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table("paymentTransaction")
public class PaymentTransaction {

    @Id
    private UUID uuid;
    @Column("payment_method")
    private PaymentMethod paymentMethod;
    @Column
    private TransactionType type;
    @Column
    private TransactionStatus status;
    @Column
    private BigDecimal amount;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("account_id")
    private Long accountBalanceId;
    @Column("customer_id")
    private Long customerId;
    @Column("language_iso")
    private String languageIso;
    @Column("currency_iso")
    private String currencyIso;
    @Column("notification_url")
    private String notificationUrl;
    @Column
    private String message;
    @Transient
    private Customer customer;
    @Transient
    private Card card;

    public static PaymentTransaction fromDto(PaymentTransactionDto transactionDto, TransactionType type, Long customerId, Long accountId) {
        return new PaymentTransaction(null,
                transactionDto.paymentMethod(),
                type,
                TransactionStatus.IN_PROGRESS,
                transactionDto.amount(),
                null,
                null,
                accountId,
                customerId,
                transactionDto.languageIso(),
                transactionDto.currencyIso(),
                transactionDto.notificationUrl(),
                null,
                null,
                null);
    }

    public static Mono<PaymentTransaction> fromRow(Map<String, Object> row) {
        return Mono.just(PaymentTransaction.builder()
                .uuid((UUID) row.get("uuid"))
                .paymentMethod(PaymentMethod.valueOf((String) row.get("payment_method")))
                .status(TransactionStatus.valueOf((String) row.get("status")))
                .type(TransactionType.valueOf((String) row.get("type")))
                .amount((BigDecimal) row.get("amount"))
                .currencyIso((String) row.get("currency_iso"))
                .languageIso((String) row.get("language_iso"))
                .notificationUrl((String) row.get("notification_url"))
                .createdAt((LocalDateTime) row.get("created_at"))
                .updatedAt((LocalDateTime) row.get("updated_at"))
                .message((String) row.get("message"))
                .card(Card.fromRow(row))
                .customer(Customer.fromRow(row))
                .build());
    }

}