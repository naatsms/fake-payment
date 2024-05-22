package naatsms.person.service;

import naatsms.person.entity.Individual;
import naatsms.person.entity.ProfileHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProfileHistoryService {

    Mono<ProfileHistory> createHistoryEntry(Individual oldUser, Individual newUser);

    Flux<ProfileHistory> getProfileHistoryForProfileId(UUID id);

}
