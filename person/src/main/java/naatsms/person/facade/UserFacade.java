package naatsms.person.facade;

import naatsms.person.dto.IndividualDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserFacade {

    Mono<IndividualDto> getIndividualById(UUID id);

    Mono<IndividualDto> createIndividual(IndividualDto individual);

    Mono<IndividualDto> updateIndividual(UUID id, IndividualDto updatedIndividual);

    Mono<Void> deleteIndividual(UUID id);

}