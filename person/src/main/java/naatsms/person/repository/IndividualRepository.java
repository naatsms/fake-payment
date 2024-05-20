package naatsms.person.repository;

import naatsms.person.entity.Individual;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualRepository extends ReactiveCrudRepository<Individual, UUID> {

    Mono<Individual> findByProfileId(UUID id);

}

