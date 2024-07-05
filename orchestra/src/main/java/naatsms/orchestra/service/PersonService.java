package naatsms.orchestra.service;

import com.naatsms.dto.IndividualDto;
import reactor.core.publisher.Mono;

public interface PersonService {
    Mono<IndividualDto> createUser(IndividualDto user);

    Mono<Void> deleteUser(IndividualDto user);

    Mono<IndividualDto> getUser(String username);
}
