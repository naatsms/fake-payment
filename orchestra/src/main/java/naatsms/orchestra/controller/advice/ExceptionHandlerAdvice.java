package naatsms.orchestra.controller.advice;

import naatsms.orchestra.exception.PasswordMismatchException;
import naatsms.orchestra.exception.SagaErrorException;
import naatsms.orchestra.exception.SagaRollbackErrorException;
import naatsms.orchestra.service.PersonService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private static Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    private final PersonService personService;

    public ExceptionHandlerAdvice(PersonService personService) {
        this.personService = personService;
    }

    @ExceptionHandler({
            PasswordMismatchException.class,
            SagaErrorException.class,
            SagaRollbackErrorException.class})
    public final Mono<ResponseEntity<Object>> handleApplicationExceptions(Exception ex, ServerWebExchange exchange) {
        return switch (ex) {
            case PasswordMismatchException passwordEx ->
                    handleExceptionInternal(passwordEx, Map.of("message", passwordEx.getMessage()), null, HttpStatus.BAD_REQUEST, exchange);
            case SagaErrorException sagaEx ->
                    handleSagaException(sagaEx, exchange);
            default -> Mono.error(new ServerErrorException("Unexpected server error:", ex));
        };
    }

    private @NotNull Mono<ResponseEntity<Object>> handleSagaRollbackException(SagaRollbackErrorException sagaRollbackEx, ServerWebExchange exchange) {
        var details = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, sagaRollbackEx.getMessage());
        return handleExceptionInternal(sagaRollbackEx, details, null, HttpStatus.INTERNAL_SERVER_ERROR, exchange);
    }

    private @NotNull Mono<ResponseEntity<Object>> handleSagaException(SagaErrorException sagaEx, ServerWebExchange exchange) {
        var details = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, sagaEx.getMessage());
        details.setProperty("response", sagaEx.getErrorBody());
        return personService.deleteUser(sagaEx.getUserDto())
                .then(handleExceptionInternal(sagaEx, details, null, HttpStatus.INTERNAL_SERVER_ERROR, exchange))
                .doOnError(SagaRollbackErrorException.class, this::logSagaRollbackFailed)
                .onErrorResume(SagaRollbackErrorException.class, ex -> handleExceptionInternal(ex, enrichDetails(details), null, HttpStatus.INTERNAL_SERVER_ERROR, exchange));
    }

    private void logSagaRollbackFailed(Throwable ex) {
        LOG.error("Error occurred while trying to rollback SAGA:", ex);
    }

    private ProblemDetail enrichDetails(ProblemDetail details) {
        details.setTitle("Unexpected error, contact tech support team");
        return details;
    }

}