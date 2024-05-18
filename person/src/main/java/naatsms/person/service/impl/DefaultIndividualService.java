package naatsms.person.service.impl;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Individual;
import naatsms.person.entity.Profile;
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
        return individualRepository.findById(uuid)
                .flatMap(this::fetchProfile);
    }

    @Override
    public Mono<Individual> createIndividual(IndividualDto individual) {
        return profileService.createProfile(individual.profile())
                .map(profile -> doCreateIndividual(individual, profile))
                .flatMap(ind -> profileHistoryService.createHistoryEntry(ind)
                        .thenReturn(ind))
                .flatMap(individualRepository::save);
    }


    private Individual doCreateIndividual(IndividualDto individual, Profile profile) {
        var entity = IndividualMapper.INSTANCE.individualFromDto(individual);
        entity.setProfileId(profile.getProfileId());
        return entity;
    }

    private Mono<Individual> fetchProfile(Individual individual) {
        return profileService.getProfileById(individual.getProfileId())
                .doOnNext(individual::setProfile)
                .then(Mono.just(individual));
    }

}
