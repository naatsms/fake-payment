package naatsms.person.repository;

import naatsms.person.entity.ProfileHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProfileHistoryRepository extends ReactiveCrudRepository<ProfileHistory, UUID> {}

