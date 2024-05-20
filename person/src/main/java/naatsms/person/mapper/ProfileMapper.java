package naatsms.person.mapper;

import naatsms.person.dto.ProfileDto;
import naatsms.person.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "type", ignore = true)
    Profile profileFromDto(ProfileDto profileDto);

    ProfileDto profileToDto(Profile profile);

}