package naatsms.orchestra.exception;

import com.naatsms.dto.IndividualDto;

import java.util.Collections;
import java.util.Map;

public class SagaErrorException extends RuntimeException {

    private final IndividualDto userDto;
    private final Map<String, Object> errorBody;

    public SagaErrorException(IndividualDto userDto, String message) {
        super(message);
        this.userDto = userDto;
        errorBody = Collections.emptyMap();
    }

    public SagaErrorException(IndividualDto userDto, Throwable ex) {
        super(ex);
        this.userDto = userDto;
        errorBody = Collections.emptyMap();
    }

    public SagaErrorException(IndividualDto userDto, Map<String,Object> errorBody) {
        this.errorBody = errorBody;
        this.userDto = userDto;
    }

    public IndividualDto getUserDto() {
        return userDto;
    }

    public Map<String, Object> getErrorBody() {
        return errorBody;
    }
}
