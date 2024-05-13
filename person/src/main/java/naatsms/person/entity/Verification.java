package naatsms.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import naatsms.person.enums.VerificationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("verification_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {

    @Id
    @Column("id")
    private UUID id;
    @Column("profile_id")
    private UUID profileId;
    @Column("profile_type")
    private String profileType;
    @Column("details")
    private String details;
    @Column("verification_status")
    private VerificationStatus verificationStatus;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;

}
