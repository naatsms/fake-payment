package naatsms.orchestra.service;

import com.naatsms.dto.IndividualDto;
import com.naatsms.dto.ProfileDto;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import naatsms.orchestra.exception.SagaErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultKeyCloakServiceTest  {

    @InjectMocks
    private DefaultKeyCloakService keyCloakService;

    @Mock
    private Keycloak client;
    @Mock
    RealmResource realm;
    @Mock
    UsersResource usersResource;
    @Mock
    Response response;
    @Mock
    private IndividualDto userDto;
    @Mock
    private ProfileDto profile;
    private UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(client.realm("payment")).thenReturn(realm);
        when(realm.users()).thenReturn(usersResource);
        when(userDto.email()).thenReturn("testEmail");
        when(userDto.profile()).thenReturn(profile);
        when(userDto.profileId()).thenReturn(uuid);
        when(profile.secretKey()).thenReturn("testSecret");

    }

    @Test
    void testRegistrationSuccess() {
        when(usersResource.create(any())).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(CREATED);
        StepVerifier.create(keyCloakService.registerClient(userDto))
                .expectNextMatches(userDto::equals).verifyComplete();
    }

    @Test
    void testRegistrationFailedBadRequest() {
        when(usersResource.create(any())).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(BAD_REQUEST);
        var responseString = "Request is bad";
        when(response.readEntity(String.class)).thenReturn(responseString);
        StepVerifier.create(keyCloakService.registerClient(userDto))
                .expectErrorMatches(ex -> assertBadRequestError(ex, responseString)).verify();
    }


    @Test
    void testRegistrationFailedExceptionally() {
        Class<WebApplicationException> aClass = WebApplicationException.class;
        when(usersResource.create(any())).thenThrow(aClass);
        StepVerifier.create(keyCloakService.registerClient(userDto))
                .expectErrorMatches(ex -> assertExceptionError(ex,aClass)).verify();
    }

    private boolean assertBadRequestError(Throwable ex, String responseString) {
        return ex instanceof SagaErrorException && ex.getMessage().equalsIgnoreCase(responseString);
    }

    private boolean assertExceptionError(Throwable ex, Class<WebApplicationException> aClass) {
        return ex instanceof SagaErrorException && aClass.isInstance(ex.getCause());
    }



}