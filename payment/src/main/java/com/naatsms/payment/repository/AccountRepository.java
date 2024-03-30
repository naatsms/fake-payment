package com.naatsms.payment.repository;

import com.naatsms.payment.entity.AccountBalance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<AccountBalance, Long>
{
    Mono<AccountBalance> findByMerchantIdAndCurrencyIso(Long merchantId, String currencyIso);
}
