package naatsms.orchestra.exception;

import com.naatsms.dto.IndividualDto;

public class SagaErrorException extends RuntimeException {

    private final IndividualDto userDto;

    public SagaErrorException(IndividualDto userDto, String message) {
        super(message);
        this.userDto = userDto;
    }

    public SagaErrorException(IndividualDto userDto, Throwable ex) {
        super(ex);
        this.userDto = userDto;
    }

    public IndividualDto getUserDto() {
        return userDto;
    }
}
