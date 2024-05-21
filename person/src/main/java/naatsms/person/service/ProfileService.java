package naatsms.person.service;

import naatsms.person.dto.ProfileDto;
import naatsms.person.entity.Profile;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProfileService {

    Mono<Profile> createProfile(ProfileDto profile);

    Mono<Profile> getProfileById(UUID id);

    Mono<Profile> archiveProfile(Profile user);
}
