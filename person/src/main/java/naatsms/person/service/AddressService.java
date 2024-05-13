package naatsms.person.service;

import naatsms.person.dto.AddressDto;
import naatsms.person.entity.Address;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressService {

    Mono<Address> createAddress(AddressDto addressDto);

    Mono<Address> getAddressById(UUID id);

}
