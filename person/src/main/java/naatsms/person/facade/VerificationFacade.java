package naatsms.person.facade;

import naatsms.person.dto.VerificationDto;
import naatsms.person.enums.ProfileType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationFacade {

    Mono<VerificationDto> createVerification(VerificationDto dto);

    Mono<VerificationDto> getVerificationStatusById(UUID id);

    Mono<VerificationDto> getVerificationByProfileIdAndType(UUID profileId, ProfileType type);

    Mono<VerificationDto> updateVerificationStatusById(UUID id, VerificationDto request);

}