package com.naatsms.payment.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface NotificationService
{
    Mono<Void> sendNotification(UUID transaction);
}
