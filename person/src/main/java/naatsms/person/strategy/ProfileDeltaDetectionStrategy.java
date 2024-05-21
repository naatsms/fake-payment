package naatsms.person.strategy;

import com.google.gson.JsonObject;
import naatsms.person.entity.Profile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
@Qualifier("profileDeltaDetectionStrategy")
public class ProfileDeltaDetectionStrategy implements DeltaDetectionStrategy<Profile> {

    @Override
    public JsonObject calculateDelta(Profile oldProfile, Profile newProfile) {
        var delta = new JsonObject();
        if (notEquals(Profile::getAddressId, oldProfile, newProfile)) {
            delta.addProperty("addressId", newProfile.getAddressId().toString());
        }
        if (notEquals(Profile::getFirstName, oldProfile, newProfile)) {
            delta.addProperty("firstName", newProfile.getFirstName());
        }
        if (notEquals(Profile::getLastName, oldProfile, newProfile)) {
            delta.addProperty("lastName", newProfile.getLastName());
        }
        if (notEquals(Profile::getArchivedAt, oldProfile, newProfile)) {
            delta.addProperty("archivedAt", newProfile.getArchivedAt().toString());
        }
        if (notEquals(Profile::getSecretKey, oldProfile, newProfile)) {
            delta.addProperty("secretKey", newProfile.getSecretKey());
        }
        if (notEquals(Profile::getStatus, oldProfile, newProfile)) {
            delta.addProperty("status", newProfile.getStatus().toString());
        }
        return delta;
    }

    private boolean notEquals(Function<Profile, Object> getter, Profile oldProfile, Profile profile) {
        return !Objects.equals(getter.apply(oldProfile), getter.apply(profile));
    }
}
