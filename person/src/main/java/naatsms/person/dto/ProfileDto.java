package naatsms.person.dto;
import naatsms.person.enums.ItemStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileDto(
        UUID profileId,
        String secretKey,
        String firstName,
        String lastName,
        ItemStatus status,
        boolean filled,
        AddressDto address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime verifiedAt,
        LocalDateTime archivedAt
) {}