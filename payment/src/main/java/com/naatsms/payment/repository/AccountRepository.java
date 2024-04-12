package com.naatsms.payment.repository;

import com.naatsms.payment.entity.AccountBalance;
import com.naatsms.payment.entity.projections.IdOnly;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountRepository extends ReactiveCrudRepository<AccountBalance, Long>
{
    Mono<AccountBalance> findByMerchantIdAndCurrencyIso(Long merchantId, String currencyIso);

    @Lock(LockMode.PESSIMISTIC_WRITE)
    @Query("SELECT * from accountbalance where id = :accountId FOR UPDATE")
    Mono<AccountBalance> selectForUpdateById(Long accountId);

    @Modifying
    @Query("UPDATE accountbalance SET amount = :amount WHERE id = :accountId")
    Mono<Void> updateAmountByAccountId(Long accountId, BigDecimal amount);

    Flux<IdOnly> findAllByMerchantId(Long merchantId);

}
