package com.naatsms.payment.entity;

import com.naatsms.payment.dto.CustomerDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Map;

@Table("customer")
public record Customer(
        @Id Long id,
        @Column("card_id") Long cardId,
        @Column("first_name") String firstName,
        @Column("last_name") String lastName,
        @Column String country)
{

    public static Customer fromDto(CustomerDto dto, Long cardId) {
        return new Customer(null,
                cardId,
                dto.firstName(),
                dto.lastName(),
                dto.country());
    }

    public static Customer fromRow(Map<String, Object> row) {
        return new Customer(
                null,
                (Long) row.get("cs_card_id"),
                (String) row.get("cs_name"),
                (String) row.get("cs_last_name"),
                (String) row.get("cs_country")
                );
    }
}
