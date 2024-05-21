package naatsms.person.facade.impl;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Individual;
import naatsms.person.enums.ProfileType;
import naatsms.person.enums.VerificationStatus;
import naatsms.person.facade.UserFacade;
import naatsms.person.mapper.IndividualMapper;
import naatsms.person.service.IndividualService;
import naatsms.person.service.VerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DefaultUserFacade implements UserFacade {

    private final VerificationService verificationService;
    private final IndividualService individualService;
    private final TransactionalOperator txOperator;

    public DefaultUserFacade(VerificationService verificationService, IndividualService individualService, TransactionalOperator txOperator) {
        this.verificationService = verificationService;
        this.individualService = individualService;
        this.txOperator = txOperator;
    }

    @Override
    public Mono<IndividualDto> getIndividualById(UUID id) {
        return individualService.getIndividualById(id)
                .map(IndividualMapper.INSTANCE::dtoFromIndividual);
    }

    @Override
    public Mono<IndividualDto> createIndividual(IndividualDto individual) {
        return individualService.createIndividual(individual)
                .flatMap(this::doCreateVerification)
                .as(txOperator::transactional)
                .map(IndividualMapper.INSTANCE::dtoFromIndividual);
    }

    private Mono<Individual> doCreateVerification(Individual ind) {
        return verificationService.createVerification(ind.getProfileId(), ProfileType.INDIVIDUAL, VerificationStatus.PENDING, "INITIAL STATUS")
                .thenReturn(ind);
    }

    @Override
    public Mono<IndividualDto> updateIndividual(UUID id, IndividualDto updatedIndividual) {
        return individualService.updateIndividual(id, updatedIndividual)
                .as(txOperator::transactional)
                .map(IndividualMapper.INSTANCE::dtoFromIndividual);
    }

    @Override
    public Mono<Void> deleteIndividual(UUID id) {
        return individualService.archiveIndividual(id);
    }
}