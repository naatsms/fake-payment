package naatsms.orchestra.exception;

public class SagaRollbackErrorException extends RuntimeException {

    public SagaRollbackErrorException(String message) {
        super(message);
    }

    public SagaRollbackErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
