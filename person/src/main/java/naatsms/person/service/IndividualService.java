package naatsms.person.service;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Individual;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualService {

    Mono<Individual> getIndividualById(UUID uuid);
    Mono<Individual> createIndividual(IndividualDto individualDto);
    Mono<Individual> updateIndividual(UUID id, IndividualDto updatedIndividual);

}