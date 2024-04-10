package com.naatsms.payment.repository.extensions;

import com.naatsms.payment.entity.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public class FindTransactionEnrichedImpl implements FindTransactionEnriched<PaymentTransaction> {

    @Autowired
    private DatabaseClient client;

    public static final String fields =
            "pt.uuid AS pt_uuid," +
            "pt.payment_method AS pt_method," +
            "pt.type AS pt_type," +
            "pt.status AS pt_status," +
            "pt.language_iso AS pt_language," +
            "pt.currency_iso AS pt_currency," +
            "pt.amount AS pt_amount," +
            "pt.created_at AS pt_created_at," +
            "pt.updated_at AS pt_updated_at," +
            "pt.message AS pt_message," +
            "pt.notification_url AS pt_url," +
            "cd.card_number AS cd_number," +
            "cd.id AS cd_id," +
            "cd.card_amount AS cd_amount," +
            "cs.first_name AS cs_name," +
            "cs.last_name AS cs_last_name" +
            "cs.country AS cs_country ";

    public static final String FIND_BY_UUID_QUERY = "SELECT " + fields + "FROM paymenttransaction AS pt " +
            "JOIN customer c ON c.id = pt.customer_id " +
            "JOIN card c2 ON c2.id = c.card_id " +
            "WHERE pt.uuid = :uuid";
    public static final String LIST_BY_DATE_RANGE_AND_MERCHANT_QUERY = "SELECT " + fields + "FROM paymenttransaction as pt " +
            "JOIN card cd on pt.amount = cd.amount " +
            "JOIN customer cs on cs.id = pt.customer_id " +
            "JOIN accountbalance a on a.id = pt.account_id " +
            "JOIN merchant m on m.id = a.merchant_id " +
            "WHERE pt.created_at >= :dateFrom " +
            "AND pt.created_at <= :dateTo " +
            "AND m.id = :merchantId";

    @Override
    public Mono<PaymentTransaction> findByUuidEnriched(UUID uuid) {
        return client.sql(FIND_BY_UUID_QUERY)
                .bind("uuid", uuid)
                .fetch()
                .first()
                .single()
                .flatMap(PaymentTransaction::fromRow);
    }

    public Flux<PaymentTransaction> findByDateRangeForMerchant(Long merchantId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return client.sql(LIST_BY_DATE_RANGE_AND_MERCHANT_QUERY)
                .bind("dateFrom", dateFrom)
                .bind("dateTo", dateTo)
                .bind("merchantId", merchantId)
                .fetch()
                .all()
                .flatMap(PaymentTransaction::fromRow);
    }

}
