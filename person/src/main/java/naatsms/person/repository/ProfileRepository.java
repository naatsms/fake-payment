package naatsms.person.repository;

import naatsms.person.entity.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProfileRepository extends ReactiveCrudRepository<Profile, UUID> {}

