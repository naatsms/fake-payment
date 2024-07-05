package com.naatsms.dto;

import java.util.UUID;

public record MerchantDto(
        UUID id,
        String email,
        String phoneNumber,
        String companyId,
        String companyName,
        ProfileDto profile
) {}

