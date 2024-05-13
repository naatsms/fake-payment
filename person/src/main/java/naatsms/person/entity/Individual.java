package naatsms.person.entity;

import lombok.*;
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
@Table("individuals")
public class Individual {

    @Id
    @Column("id")
    private UUID id;
    @Column("profile_id")
    private UUID profileId;
    @Column("passport_number")
    private String passportNumber;
    @Column("phone_number")
    private String phoneNumber;
    @Column("email")
    private String email;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Transient
    @Setter
    private Profile profile;
}
