package naatsms.person.service.impl;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Address;
import naatsms.person.entity.Individual;
import naatsms.person.entity.Profile;
import naatsms.person.enums.ItemStatus;
import naatsms.person.enums.ProfileType;
import naatsms.person.mapper.IndividualMapper;
import naatsms.person.repository.IndividualRepository;
import naatsms.person.service.IndividualService;
import naatsms.person.service.ProfileHistoryService;
import naatsms.person.service.ProfileService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DefaultIndividualService implements IndividualService {

    private final ProfileService profileService;
    private final IndividualRepository individualRepository;
    private final ProfileHistoryService profileHistoryService;

    public DefaultIndividualService(ProfileService profileService, IndividualRepository repository, ProfileHistoryService profileHistoryService) {
        this.profileService = profileService;
        this.individualRepository = repository;
        this.profileHistoryService = profileHistoryService;
    }

    @Override
    public Mono<Individual> getIndividualById(UUID uuid) {
        return individualRepository.findByProfileId(uuid)
                .flatMap(this::fetchProfile)
                .filter(user -> user.getProfile().getStatus().equals(ItemStatus.ACTIVE))
                .switchIfEmpty(Mono.error(IllegalArgumentException::new));
    }

    @Override
    public Mono<Individual> createIndividual(IndividualDto individual) {
        return profileService.createProfile(individual.profile())
                .map(profile -> doCreateIndividual(individual, profile))
                .flatMap(user -> profileHistoryService.createHistoryEntry(emptyUser(), user)
                        .thenReturn(user))
                .flatMap(individualRepository::save);
    }

    private Individual emptyUser() {
        return Individual.builder()
                .profile(Profile.builder().address(new Address()).build())
                .build();
    }

    private Individual doCreateIndividual(IndividualDto individual, Profile profile) {
        var entity = IndividualMapper.INSTANCE.individualFromDto(individual);
        profile.setType(ProfileType.INDIVIDUAL);
        entity.setProfileId(profile.getId());
        entity.setProfile(profile);
        return entity;
    }

    @Override
    public Mono<Individual> updateIndividual(UUID id, IndividualDto updatedIndividual) {
        var mapper = IndividualMapper.INSTANCE;
        return getIndividualById(id)
                .switchIfEmpty(Mono.error(IllegalArgumentException::new))
                .zipWhen(old -> Mono.just(mapper.updateFromDto(mapper.clone(old), updatedIndividual)))
                .flatMap(tuple -> profileHistoryService.createHistoryEntry(tuple.getT1(), tuple.getT2()).thenReturn(tuple.getT2()))
                .flatMap(individualRepository::save);
    }

    @Override
    public Mono<Void> archiveIndividual(UUID id) {
        return getIndividualById(id)
                .map(Individual::getProfile)
                .flatMap(profileService::archiveProfile)
                .then();
    }

    private Mono<Individual> fetchProfile(Individual individual) {
        return profileService.getProfileById(individual.getProfileId())
                .doOnNext(individual::setProfile)
                .then(Mono.just(individual));
    }

}
