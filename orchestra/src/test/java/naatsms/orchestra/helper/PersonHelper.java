package naatsms.orchestra.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naatsms.dto.IndividualDto;
import com.naatsms.dto.ProfileDto;
import com.naatsms.enums.ItemStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PersonHelper {

    private PersonHelper() {
    }

    public static @NotNull String getMockResponseBody(UUID uuid, String email, String password) {
        var dto = getMockDto(uuid, email, password);
        try {
            return new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull IndividualDto getMockDto(UUID uuid, String email, String secret) {
        return new IndividualDto(uuid, "test", "test", email, new ProfileDto(uuid, secret, "test", "test", ItemStatus.ACTIVE, false, null, null, null, null, null));
    }

}
