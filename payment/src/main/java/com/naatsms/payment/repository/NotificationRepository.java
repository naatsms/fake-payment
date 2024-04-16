package com.naatsms.payment.repository;

import com.naatsms.payment.entity.NotificationLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationLog, Long>
{
}
