package naatsms.person.repository;

import naatsms.person.entity.MerchantMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MerchantMembersRepository extends ReactiveCrudRepository<MerchantMember, UUID> {}

