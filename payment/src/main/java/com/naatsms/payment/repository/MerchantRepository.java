package com.naatsms.payment.repository;

import com.naatsms.payment.entity.Merchant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MerchantRepository extends ReactiveCrudRepository<Merchant, Long>
{
    Mono<Merchant> findByName(String name);
}
