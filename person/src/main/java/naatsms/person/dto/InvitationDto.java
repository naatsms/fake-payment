package naatsms.person.dto;

import naatsms.person.enums.ItemStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvitationDto(
        UUID id,
        UUID merchantId,
        String firstName,
        String lastName,
        String email,
        ItemStatus status,
        LocalDateTime created,
        LocalDateTime expires
) {}
