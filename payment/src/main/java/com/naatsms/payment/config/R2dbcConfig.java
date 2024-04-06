package com.naatsms.payment.config;

import io.r2dbc.spi.ConnectionFactories;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
public class R2dbcConfig
{

    @Bean
    ReactiveTransactionManager reactiveTransactionManager(R2dbcConnectionDetails details) {
        return new R2dbcTransactionManager(ConnectionFactories.get(details.getConnectionFactoryOptions()));
    }

    @Bean
    TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

}
