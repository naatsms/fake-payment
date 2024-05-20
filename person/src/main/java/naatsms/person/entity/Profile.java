package naatsms.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import naatsms.person.enums.ItemStatus;
import naatsms.person.enums.ProfileType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("person.profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @Column("id")
    private UUID id;
    @Column("secret_key")
    private String secretKey;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("status")
    private ItemStatus status;
    @Column("filled")
    private boolean filled;
    @Column("address_id")
    private UUID addressId;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("verified_at")
    private LocalDateTime verifiedAt;
    @Column("archived_at")
    private LocalDateTime archivedAt;
    @Column("type")
    private ProfileType type;
    @Transient
    private Address address;
}
