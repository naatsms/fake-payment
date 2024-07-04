package naatsms.person.service.impl;

import naatsms.person.dto.AddressDto;
import naatsms.person.entity.Address;
import naatsms.person.mapper.AddressMapper;
import naatsms.person.repository.AddressRepository;
import naatsms.person.service.AddressService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DefaultAddressService implements AddressService {

    private final AddressRepository addressRepository;

    public DefaultAddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Mono<Address> createAddress(AddressDto address) {
        return Mono.just(address != null ? address : new AddressDto(null, null, null, null, null, null, null, null, null))
                .map(this::addressFromDto)
                .flatMap(addressRepository::save);
    }

    private Address addressFromDto(AddressDto addr) {
        return AddressMapper.INSTANCE.addressFromDto(addr);
    }

    @Override
    public Mono<Address> getAddressById(UUID id) {
        return addressRepository.findById(id);
    }

}
