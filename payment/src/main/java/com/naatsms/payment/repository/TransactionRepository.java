package com.naatsms.payment.repository;

import com.naatsms.payment.entity.PaymentTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<PaymentTransaction, UUID>
{}
