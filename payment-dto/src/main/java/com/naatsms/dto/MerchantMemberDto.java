package com.naatsms.dto;

import java.util.UUID;

public record MerchantMemberDto(
        UUID id,
        UUID merchantId,
        String phoneNumber,
        String memberRole,
        ProfileDto profile
) {}
