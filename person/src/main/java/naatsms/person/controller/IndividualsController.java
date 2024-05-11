package naatsms.person.controller;

import naatsms.person.dto.IndividualDto;
import naatsms.person.facade.UserFacade;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/individuals")
public class IndividualsController {

    private final UserFacade userFacade;

    public IndividualsController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/{id}")
    public Mono<IndividualDto> getIndividualById(@PathVariable String id) {
        return userFacade.getIndividualById(id);
    }

    @PostMapping
    public Mono<IndividualDto> createIndividual(@RequestBody IndividualDto individual) {
        return userFacade.createIndividual(individual);
    }

    @PutMapping("/{id}")
    public Mono<IndividualDto> updateIndividual(@PathVariable String id, @RequestBody IndividualDto updatedIndividual) {
        return userFacade.updateIndividual(id, updatedIndividual);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteIndividual(@PathVariable String id) {
        return userFacade.deleteIndividual(id);
    }

}