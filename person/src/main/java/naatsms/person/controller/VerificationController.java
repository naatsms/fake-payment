package naatsms.person.controller;


import naatsms.person.dto.VerificationDto;
import naatsms.person.enums.ProfileType;
import naatsms.person.facade.VerificationFacade;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

//@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    private final VerificationFacade verificationFacade;

    public VerificationController(VerificationFacade verificationFacade) {
        this.verificationFacade = verificationFacade;
    }

    @PostMapping
    public Mono<VerificationDto> createVerification(VerificationDto dto) {
        return verificationFacade.createVerification(dto);
    }

    @GetMapping("/{id}")
    public Mono<VerificationDto> getVerificationStatusById(@PathVariable UUID id) {
        return verificationFacade.getVerificationStatusById(id);
    }

    @GetMapping
    public Mono<VerificationDto> getVerificationStatusByProfileIdAndType(@RequestParam("id") UUID profileId, @RequestParam("type") ProfileType type) {
        return verificationFacade.getVerificationByProfileIdAndType(profileId, type);
    }

    @PutMapping("/{id}")
    public Mono<VerificationDto> updateVerificationStatusById(@PathVariable UUID id, @RequestBody VerificationDto request) {
        return verificationFacade.updateVerificationStatusById(id, request);
    }

}
