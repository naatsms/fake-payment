package naatsms.person.mapper;

import naatsms.person.dto.ProfileDto;
import naatsms.person.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "filled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    Profile profileFromDto(ProfileDto profileDto);

    ProfileDto profileToDto(Profile profile);

    @InheritConfiguration
    Profile updateFromDto(@MappingTarget Profile profile, ProfileDto dto);

    @DeepClone
    Profile clone(Profile profile);

}