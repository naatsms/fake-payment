package naatsms.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("merchants")
public class Merchant {

    @Id
    @Column("id")
    private UUID id;
    @Column("profile_id")
    private UUID profileId;
    @Column("company_name")
    private String companyName;
    @Column("company_id")
    private String companyId;
    @Column("email")
    private String email;
    @Column("phone_number")
    private String phoneNumber;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private Profile profile;
}
