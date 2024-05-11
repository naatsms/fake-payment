package naatsms.person.dto;

import naatsms.person.enums.ItemStatus;

import java.time.LocalDateTime;

public record CountryDto(
        Long id,
        String name,
        String alpha2,
        String alpha3,
        ItemStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}