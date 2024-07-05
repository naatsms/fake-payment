package naatsms.orchestra.service;

import com.naatsms.dto.IndividualDto;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import reactor.core.publisher.Mono;

public interface KeyCloakService {
    Mono<IndividualDto> registerClient(IndividualDto userDto);

    Mono<AuthorizationResponse> authenticateClient(IndividualDto individualDto);
}
