package naatsms.person.strategy;

import com.google.gson.JsonObject;
import naatsms.person.entity.Individual;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
@Qualifier("individualDeltaDetectionStrategy")
public class IndividualDeltaDetectionStrategy implements DeltaDetectionStrategy<Individual> {

    @Override
    public JsonObject calculateDelta(Individual oldItem, Individual newItem) {
        var delta = new JsonObject();
        for (IndividualFields field : IndividualFields.values()) {
            if (notEquals(oldItem, newItem, field.getter)) {
                delta.addProperty(field.name(), field.getter.apply(newItem).toString());
            }
        }
        return delta;
    }

    private boolean notEquals(Individual oldProfile, Individual profile, Function<Individual, Object> getter) {
        return !Objects.equals(getter.apply(oldProfile), getter.apply(profile));
    }

    private enum IndividualFields {
        EMAIL(Individual::getEmail),
        PASSPORT_NUMBER(Individual::getPassportNumber),
        PHONE_NUMBER(Individual::getPhoneNumber);

        private final Function<Individual, Object> getter;

        IndividualFields(Function<Individual, Object> getter) {
            this.getter = getter;
        }
    }

}