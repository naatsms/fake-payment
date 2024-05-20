package naatsms.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("person.merchant_members")
public class MerchantMember {

    @Column("profile_id")
    private UUID profileId;
    @Column("merchant_id")
    private UUID merchantId;
    @Column("member_role")
    private String memberRole;

    @Transient
    private Profile profile;
}
