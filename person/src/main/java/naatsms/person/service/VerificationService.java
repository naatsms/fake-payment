package naatsms.person.service;

import naatsms.person.entity.Verification;
import naatsms.person.enums.ProfileType;
import naatsms.person.enums.VerificationStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationService {

    Mono<Verification> createVerification(UUID profileId, ProfileType profileType, VerificationStatus status, String details);

}