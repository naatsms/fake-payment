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

@Table("profile_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileHistory {

    @Id
    @Column("id")
    private UUID id;
    @Column("profile_id")
    private UUID profileId;
    @Column("profile_type")
    private String profileType;
    @Column("reason")
    private String reason;
    @Column("comment")
    private String comment;
    @Column("changed_values")
    private String changedValues;
    @Column("created_at")
    private LocalDateTime createdAt;

}
