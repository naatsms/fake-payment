package naatsms.person.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import naatsms.person.enums.ProfileType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileHistoryDto(
        UUID id,
        UUID profileId,
        ProfileType profileType,
        String reason,
        String comment,
        @JsonRawValue String changedValues,
        LocalDateTime createdAt
) {}
