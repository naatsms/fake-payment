package com.naatsms.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("notificationLog")
public record NotificationLog (
    @Id @Column Long id,
    @Column("transaction_uuid") UUID transactionId,
    @Column("request_payload") String requestPayload,
    @Column("response_payload") String responsePayload,
    @Column("response_status") int responseStatus,
    @Column("url") String url
) {}