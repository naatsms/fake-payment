package naatsms.person.service.impl;

import com.google.gson.JsonObject;
import io.r2dbc.postgresql.codec.Json;
import naatsms.person.entity.Address;
import naatsms.person.entity.Individual;
import naatsms.person.entity.Profile;
import naatsms.person.entity.ProfileHistory;
import naatsms.person.repository.ProfileHistoryRepository;
import naatsms.person.service.ProfileHistoryService;
import naatsms.person.strategy.DeltaDetectionStrategy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DefaultProfileHistoryService implements ProfileHistoryService {

    private final ProfileHistoryRepository profileHistoryRepository;
    private final DeltaDetectionStrategy<Profile> profileDeltaDetectionStrategy;
    private final DeltaDetectionStrategy<Individual> individualDeltaDetectionStrategy;
    private final DeltaDetectionStrategy<Address> addressDeltaDetectionStrategy;

    public DefaultProfileHistoryService(ProfileHistoryRepository profileHistoryRepository,
                                        DeltaDetectionStrategy<Profile> profileDeltaDetectionStrategy,
                                        DeltaDetectionStrategy<Individual> individualDeltaDetectionStrategy,
                                        DeltaDetectionStrategy<Address> addressDeltaDetectionStrategy) {
        this.profileHistoryRepository = profileHistoryRepository;
        this.profileDeltaDetectionStrategy = profileDeltaDetectionStrategy;
        this.individualDeltaDetectionStrategy = individualDeltaDetectionStrategy;
        this.addressDeltaDetectionStrategy = addressDeltaDetectionStrategy;
    }

    @Override
    public Mono<ProfileHistory> createHistoryEntry(Individual oldUser, Individual newUser) {
        return calculateDelta(oldUser, newUser)
                .flatMap(json -> toProfileHistory(json, newUser.getProfile()))
                .flatMap(profileHistoryRepository::save);
    }

    private Mono<ProfileHistory> toProfileHistory(JsonObject json, Profile profile) {
        return Mono.just(ProfileHistory.builder()
                .profileId(profile.getId())
                .changedValues(Json.of(json.toString()))
                .profileType(profile.getType())
                .build());
    }

    private JsonObject mergeJson(JsonObject delta, JsonObject delta2) {
        delta.asMap().forEach(delta2::add);
        return delta2;
    }

    private Mono<JsonObject> calculateDelta(Profile oldProfile, Profile newProfile) {
        return Mono.just(profileDeltaDetectionStrategy.calculateDelta(oldProfile, newProfile))
                .zipWith(calculateDelta(oldProfile.getAddress(), newProfile.getAddress()), this::mergeJson);
    }

    private Mono<JsonObject> calculateDelta(Address oldAddress, Address newAddress) {
        return Mono.just(addressDeltaDetectionStrategy.calculateDelta(oldAddress, newAddress));
    }

    private Mono<JsonObject> calculateDelta(Individual oldProfile, Individual newProfile) {
        return Mono.just(individualDeltaDetectionStrategy.calculateDelta(oldProfile, newProfile))
                .zipWith(calculateDelta(oldProfile.getProfile(), newProfile.getProfile()), this::mergeJson);
    }

    @Override
    public Flux<ProfileHistory> getProfileHistoryForProfileId(UUID id) {
        return profileHistoryRepository.findAllByProfileId(id);
    }

}