package naatsms.person.service.impl;

import naatsms.person.dto.ProfileDto;
import naatsms.person.entity.Address;
import naatsms.person.entity.Profile;
import naatsms.person.mapper.ProfileMapper;
import naatsms.person.repository.ProfileRepository;
import naatsms.person.service.AddressService;
import naatsms.person.service.ProfileService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DefaultProfileService implements ProfileService {

    private final AddressService addressService;
    private final ProfileRepository profileRepository;

    public DefaultProfileService(AddressService addressService, ProfileRepository profileRepository) {
        this.addressService = addressService;
        this.profileRepository = profileRepository;
    }

    @Override
    public Mono<Profile> createProfile(ProfileDto profile) {
        return addressService.createAddress(profile.address())
                .map(address -> profileFromDto(profile, address))
                .flatMap(profileRepository::save);
    }

    @Override
    public Mono<Profile> getProfileById(UUID id) {
        return profileRepository.findById(id)
                .flatMap(this::fetchAddress);
    }

    private Mono<Profile> fetchAddress(Profile profile) {
        return addressService.getAddressById(profile.getAddressId())
                .doOnNext(profile::setAddress)
                .then(Mono.just(profile));
    }

    private Profile profileFromDto(ProfileDto profileDto, Address address) {
        var profile = ProfileMapper.INSTANCE.profileFromDto(profileDto);
        profile.setAddress(address);
        profile.setAddressId(address.getId());
        return profile;
    }

}