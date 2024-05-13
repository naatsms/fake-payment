package naatsms.person.repository;

import naatsms.person.entity.Individual;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface IndividualRepository extends ReactiveCrudRepository<Individual, UUID> {}

