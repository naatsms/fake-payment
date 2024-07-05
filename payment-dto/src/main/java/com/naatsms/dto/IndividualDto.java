package com.naatsms.dto;
import java.util.UUID;

public record IndividualDto(
        UUID profileId,
        String passportNumber,
        String phoneNumber,
        String email,
        ProfileDto profile
) {

    public IndividualDto(String email, String secret) {
        this(null, null, null, email, new ProfileDto(secret));
    }

}

