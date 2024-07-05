package naatsms.orchestra.controller;

import com.naatsms.dto.IndividualDto;
import jakarta.annotation.Resource;
import naatsms.orchestra.service.DefaultPersonService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static naatsms.orchestra.constants.Constants.CLAIM_UUID;

@RestController
@RequestMapping("/v1/users")
public class UsersController {

    @Resource
    private DefaultPersonService personService;

    @GetMapping
    public Mono<IndividualDto> getUserData(@AuthenticationPrincipal Jwt principal) {
        return personService.getUser(principal.getClaim(CLAIM_UUID));
    }

}
