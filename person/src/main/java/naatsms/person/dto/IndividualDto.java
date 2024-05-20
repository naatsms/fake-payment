package naatsms.person.dto;
import java.util.UUID;

public record IndividualDto(
        UUID profileId,
        String passportNumber,
        String phoneNumber,
        String email,
        ProfileDto profile
) {}

