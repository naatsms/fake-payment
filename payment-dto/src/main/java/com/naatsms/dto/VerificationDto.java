package com.naatsms.dto;

import com.naatsms.enums.ProfileType;
import com.naatsms.enums.VerificationStatus;

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