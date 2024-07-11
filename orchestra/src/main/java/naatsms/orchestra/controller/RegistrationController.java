package naatsms.orchestra.controller;

import com.naatsms.dto.IndividualDto;
import jakarta.annotation.Resource;
import naatsms.orchestra.constants.dto.AuthenticationRequest;
import naatsms.orchestra.constants.dto.AuthenticationResponse;
import naatsms.orchestra.constants.dto.RegistrationRequest;
import naatsms.orchestra.exception.PasswordMismatchException;
import naatsms.orchestra.service.KeyCloakService;
import naatsms.orchestra.service.PersonService;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/auth")
public class RegistrationController {

    @Resource
    private KeyCloakService keyCloakService;
    @Resource
    private PersonService personService;

    @PostMapping(value = "/registration")
    @ResponseStatus(HttpStatus.CREATED)
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

    private IndividualDto buildDto(RegistrationRequest request) {
        return new IndividualDto(request.username(), request.password());
    }

    private IndividualDto buildDto(AuthenticationRequest request) {
        return new IndividualDto(request.username(), request.password());
    }

}