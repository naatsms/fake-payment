package naatsms.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("addresses")
public class Address {
    @Id
    private UUID id;

    @Column("country_id")
    private Long countryId;

    @Column("address")
    private String address;

    @Column("zip_code")
    private String zipCode;

    @Column("city")
    private String city;

    @Column("state")
    private String state;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("archived_at")
    private LocalDateTime archivedAt;

}