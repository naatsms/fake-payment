package naatsms.person.service.impl;

import naatsms.person.entity.Verification;
import naatsms.person.enums.ProfileType;
import naatsms.person.enums.VerificationStatus;
import naatsms.person.repository.ProfileRepository;
import naatsms.person.repository.VerificationRepository;
import naatsms.person.service.VerificationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DefaultVerificationService implements VerificationService {

    private final VerificationRepository verificationRepository;
    private final ProfileRepository profileRepository;

    public DefaultVerificationService(VerificationRepository verificationRepository, ProfileRepository profileRepository) {
        this.verificationRepository = verificationRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public Mono<Verification> createVerification(UUID profileId, ProfileType profileType, VerificationStatus status, String details) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(IllegalArgumentException::new))
                .then(Mono.just(Verification.builder()
                       .verificationStatus(status)
                       .profileId(profileId)
                       .profileType(profileType)
                       .details(details).build()))
                .flatMap(verificationRepository::save);
    }

}