package naatsms.orchestra.service;

import com.naatsms.dto.IndividualDto;
import com.naatsms.dto.ProfileDto;
import com.naatsms.enums.ItemStatus;
import naatsms.orchestra.exception.SagaRollbackErrorException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class DefaultPersonService implements PersonService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultPersonService.class);
    public static final String CREATE_USER_URI = "/api/individuals";
    public static final String GET_USER_URI = "/api/individuals/{id}";

    private final WebClient webClient;

    public DefaultPersonService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<IndividualDto> createUser(IndividualDto user) {
        return webClient.post()
                .uri(CREATE_USER_URI)
                .bodyValue(user).retrieve()
                .bodyToMono(IndividualDto.class)
                .doOnError(e -> LOG.error("error", e));
    }

    @Override
    public Mono<Void> deleteUser(IndividualDto user) {
        return webClient.put()
                .uri(GET_USER_URI, user.profileId())
                .bodyValue(getBodyForRemoval())
                .retrieve()
                .onStatus(HttpStatusCode::isError, toSagaRollbackError())
                .bodyToMono(Void.class)
                .onErrorMap(Predicate.not(SagaRollbackErrorException.class::isInstance), toSagaRollBackErrorFromException());
    }

    private static @NotNull Function<Throwable, Throwable> toSagaRollBackErrorFromException() {
        return ex -> new SagaRollbackErrorException(ex.getMessage(), ex);
    }

    private @NotNull Function<ClientResponse, Mono<? extends Throwable>> toSagaRollbackError() {
        return response -> response.toEntity(String.class)
                .map(entity -> Optional.ofNullable(entity.getBody()).orElse(""))
                .flatMap(body -> Mono.error(new SagaRollbackErrorException(body)));
    }

    private static IndividualDto getBodyForRemoval() {
        return new IndividualDto(null, null, null, null, new ProfileDto(null, null, null, null, ItemStatus.DELETED, false, null, null, null, null, null));
    }

    @Override
    public Mono<IndividualDto> getUser(String uuid) {
        return webClient.get()
                .uri(GET_USER_URI, uuid)
                .retrieve()
                .bodyToMono(IndividualDto.class);
    }

}