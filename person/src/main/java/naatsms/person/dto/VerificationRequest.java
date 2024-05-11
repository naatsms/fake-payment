package naatsms.person.dto;

import naatsms.person.enums.ProfileType;
import naatsms.person.enums.VerificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationDto(
        UUID id,
        UUID profileId,
        ProfileType profileType,
        String details,
        VerificationStatus verificationStatus,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {}