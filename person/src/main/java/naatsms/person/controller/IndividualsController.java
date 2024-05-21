package naatsms.person.controller;

import naatsms.person.dto.IndividualDto;
import naatsms.person.facade.UserFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/individuals")
public class IndividualsController {

    private final UserFacade userFacade;

    public IndividualsController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/{id}")
    public Mono<IndividualDto> getIndividualById(@PathVariable UUID id) {
        return userFacade.getIndividualById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IndividualDto> createIndividual(@RequestBody IndividualDto individual) {
        return userFacade.createIndividual(individual);
    }

    @PutMapping("/{id}")
    public Mono<IndividualDto> updateIndividual(@PathVariable UUID id, @RequestBody IndividualDto updatedIndividual) {
        return userFacade.updateIndividual(id, updatedIndividual);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteIndividual(@PathVariable UUID id) {
        return userFacade.deleteIndividual(id);
    }

}