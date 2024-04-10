package com.naatsms.payment.entity;

import com.naatsms.payment.dto.CardDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("Card")
public class Card {
        @Id private Long id;
        @Column("card_number") private String cardNumber;
        @Column("exp_date") private LocalDate expDate;
        @Column private String ccv;
        @Column("card_amount") private BigDecimal amount;

    public static Card fromDto(CardDto cardDto) {
        return new Card(null, cardDto.cardNumber(),
                LocalDate.ofInstant(cardDto.expDate().toInstant(), ZoneId.systemDefault()),
                cardDto.ccv(), null);
    }

    public static Card fromRow(Map<String, Object> row) {
        return Card.builder()
                .id(((Integer)(row.get("cd_id"))).longValue())
                .cardNumber((String) row.get("cd_number"))
                .amount((BigDecimal) row.get("cd_amount")).build();
    }

}