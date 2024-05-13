package naatsms.person.repository;

import naatsms.person.entity.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AddressRepository extends ReactiveCrudRepository<Address, UUID> {}

