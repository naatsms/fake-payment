package naatsms.person.dto;
import java.util.UUID;

public record IndividualDto(
        UUID id,
        String passportNumber,
        String phoneNumber,
        String email,
        ProfileDto profile
) {}

