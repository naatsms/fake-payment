package com.naatsms.dto;

import com.naatsms.enums.ItemStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileDto(
        UUID id,
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
) {

    public ProfileDto(String secretKey, String firstName, String lastName, AddressDto address) {
        this(null, secretKey, firstName, lastName, ItemStatus.ACTIVE, false, address, null, null, null, null);
    }

    public ProfileDto(String secretKey) {
        this(null, secretKey, null, null, null, false, null, null, null, null, null);
    }

    public ProfileDto(ItemStatus status) {
        this(null, null, "", "", status, false, null, null, null, null, null);
    }

}