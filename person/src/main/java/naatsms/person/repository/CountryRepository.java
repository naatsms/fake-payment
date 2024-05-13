package naatsms.person.repository;

import naatsms.person.entity.Country;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CountryRepository extends ReactiveCrudRepository<Country, Long> {}

