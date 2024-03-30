package com.naatsms.payment.repository;

import com.naatsms.payment.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long>
{
    Mono<Customer> findByFirstNameAndLastNameAndCardId(String firstName, String lastName, Long cardId);
}
