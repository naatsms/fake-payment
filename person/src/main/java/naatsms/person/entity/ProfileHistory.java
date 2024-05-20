package naatsms.person.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import naatsms.person.enums.ProfileType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("person.profile_history")
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
    private ProfileType profileType;
    @Column("reason")
    private String reason;
    @Column("comment")
    private String comment;
    @Column("changed_values")
    private Json changedValues;
    @Column("created_at")
    private LocalDateTime createdAt;

}
