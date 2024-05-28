package naatsms.person.strategy;

import com.google.gson.JsonObject;
import naatsms.person.entity.Address;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
@Qualifier("individualDeltaDetectionStrategy")
public class AddressDeltaDetectionStrategy implements DeltaDetectionStrategy<Address> {

    @Override
    public JsonObject calculateDelta(Address oldItem, Address newItem) {
        var delta = new JsonObject();
        for (AddressFields field : AddressFields.values()) {
            if (notEquals(oldItem, newItem, field.getter)) {
                delta.addProperty(field.name(), field.getter.apply(newItem).toString());
            }
        }
        return delta;
    }

    private boolean notEquals(Address oldAddress, Address newAddress, Function<Address, Object> getter) {
        return !Objects.equals(getter.apply(oldAddress), getter.apply(newAddress));
    }

    private enum AddressFields {
        LINE(Address::getAddressLine, "address.addressLine"),
        CITY(Address::getCity, "address.city"),
        COUNTRY(Address::getCountryId, "address.country"),
        STATE(Address::getState, "address.state"),
        ZIPCODE(Address::getZipCode, "address.zipCode");

        private final Function<Address, Object> getter;
        private final String name;

        AddressFields(Function<Address, Object> getter, String name) {
            this.getter = getter;
            this.name = name;
        }
    }

}