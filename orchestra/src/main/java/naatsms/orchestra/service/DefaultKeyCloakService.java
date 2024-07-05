package naatsms.orchestra.service;

import com.naatsms.dto.IndividualDto;
import jakarta.ws.rs.core.Response;
import naatsms.orchestra.exception.SagaErrorException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static naatsms.orchestra.constants.Constants.CLAIM_UUID;
import static naatsms.orchestra.constants.Constants.REALM_NAME;
import static naatsms.orchestra.service.DefaultPersonService.LOG;

@Service
public class DefaultKeyCloakService implements KeyCloakService {

    private final Keycloak keycloakClient;

    public DefaultKeyCloakService(Keycloak keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    @Override
    public Mono<IndividualDto> registerClient(IndividualDto userDto) {
        UserRepresentation user = getUserRepresentation(userDto);
        try (keycloakClient) {
            Response response = keycloakClient.realm(REALM_NAME).users().create(user);
            try (response) {
                if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                    return Mono.just(userDto);
                }
                else {
                    LOG.error("[KeyCloak] CreateUser request failed");
                    return Mono.error(new SagaErrorException(userDto, response.readEntity(String.class)));
                }
            }
        } catch (Exception e) {
            LOG.error("[KeyCloak] CreateUser request failed with exception:", e);
            return Mono.error(new SagaErrorException(userDto, e));
        }
    }

    private UserRepresentation getUserRepresentation(IndividualDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(userDto.email());
        user.setUsername(user.getEmail());
        user.setCredentials(createPasswordCredential(userDto.profile().secretKey()));
        user.setAttributes(Map.of(CLAIM_UUID, List.of(userDto.profileId().toString())));
        return user;
    }

    private List<CredentialRepresentation> createPasswordCredential(String s) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(s);
        return List.of(credentials);
    }

    @Override
    public Mono<AuthorizationResponse> authenticateClient(IndividualDto individualDto) {
        try {
            LOG.info("Body: {}", individualDto);
            return Mono.just(AuthzClient.create()
                    .authorization(individualDto.email(), individualDto.profile().secretKey())
                    .authorize());
        } catch (Exception e) {
            LOG.error("shit happened: ", e); //TODO
            return Mono.error(e);
        }
    }

}