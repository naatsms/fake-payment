package naatsms.person.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddressDto(
        UUID id,
        Long countryId,
        String address,
        String zipCode,
        String city,
        String state,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime archivedAt
) {}