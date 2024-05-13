package naatsms.person.repository;

import naatsms.person.entity.Verification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface VerificationRepository extends ReactiveCrudRepository<Verification, UUID> {}

