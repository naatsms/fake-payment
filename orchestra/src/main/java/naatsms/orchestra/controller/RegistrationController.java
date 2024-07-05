package naatsms.orchestra.controller;

import com.naatsms.dto.IndividualDto;
import jakarta.annotation.Resource;
import naatsms.orchestra.constants.dto.AuthenticationRequest;
import naatsms.orchestra.constants.dto.AuthenticationResponse;
import naatsms.orchestra.constants.dto.RegistrationRequest;
import naatsms.orchestra.exception.PasswordMismatchException;
import naatsms.orchestra.exception.SagaErrorException;
import naatsms.orchestra.exception.SagaRollbackErrorException;
import naatsms.orchestra.service.DefaultPersonService;
import naatsms.orchestra.service.KeyCloakService;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/auth")
public class RegistrationController {

    @Resource
    private KeyCloakService keyCloakService;
    @Resource
    private DefaultPersonService personService;

    @PostMapping(value = "/registration")
    public Mono<AuthenticationResponse> registerUser(@RequestBody RegistrationRequest request) {
        return validate(request)
                .map(this::buildDto)
                .flatMap(personService::createUser)
                .flatMap(keyCloakService::registerClient)
                .flatMap(keyCloakService::authenticateClient)
                .map(this::mapResponse);
    }

    private Mono<RegistrationRequest> validate(RegistrationRequest request) {
        if (request.password().equals(request.confirmPassword())) {
            return Mono.just(request);
        }
        return Mono.error(PasswordMismatchException::new);
    }

    @PostMapping("/authorize")
    public Mono<AuthenticationResponse> authorizeUser(@RequestBody AuthenticationRequest request) {
        return Mono.just(request)
                .map(this::buildDto)
                .flatMap(keyCloakService::authenticateClient)
                .map(this::mapResponse);
    }

    private AuthenticationResponse mapResponse(AuthorizationResponse tokenResponse) {
        return new AuthenticationResponse(tokenResponse.getToken(),
                tokenResponse.getExpiresIn(),
                tokenResponse.getRefreshToken(),
                tokenResponse.getTokenType());
    }

    @ExceptionHandler(SagaErrorException.class)
    public Mono<ErrorResponse> rollback(SagaErrorException e) {
        return personService.deleteUser(e.getUserDto())
                .then(Mono.error(new ServerErrorException("Error during user registration: ", e.getCause())));
    }

    @ExceptionHandler(SagaRollbackErrorException.class)
    public Mono<ErrorResponse> rollback(SagaRollbackErrorException e) {
        return Mono.error(new ServerErrorException("Unexpected exception, contact tech support: ", e.getCause()));
    }

    private IndividualDto buildDto(RegistrationRequest request) {
        return new IndividualDto(request.username(), request.password());
    }

    private IndividualDto buildDto(AuthenticationRequest request) {
        return new IndividualDto(request.username(), request.password());
    }

}