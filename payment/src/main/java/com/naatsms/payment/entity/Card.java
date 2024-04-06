package com.naatsms.payment.entity;

import com.naatsms.payment.dto.CardDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Table("Card")
public record Card(
        @Id Long id,
        @Column("card_number") String cardNumber,
        @Column("exp_date") LocalDate expDate,
        String ccv,
        BigDecimal amount)
{

    public static Card fromDto(CardDto cardDto) {
        return new Card(null,
                cardDto.cardNumber(),
                LocalDate.ofInstant(cardDto.expDate().toInstant(), ZoneId.systemDefault()),
                cardDto.ccv(),
                null);
    }

}