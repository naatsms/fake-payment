package naatsms.person.facade;

import naatsms.person.dto.IndividualDto;
import reactor.core.publisher.Mono;

public interface UserFacade {

    Mono<IndividualDto> getIndividualById(String id);

    Mono<IndividualDto> createIndividual(IndividualDto individual);

    Mono<IndividualDto> updateIndividual(String id, IndividualDto updatedIndividual);

    Mono<Void> deleteIndividual(String id);

}
