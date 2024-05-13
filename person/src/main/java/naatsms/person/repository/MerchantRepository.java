package naatsms.person.repository;

import naatsms.person.entity.Merchant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MerchantRepository extends ReactiveCrudRepository<Merchant, UUID> {}

