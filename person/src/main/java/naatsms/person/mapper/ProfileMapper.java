package naatsms.person.mapper;

import naatsms.person.dto.ProfileDto;
import naatsms.person.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    Profile profileFromDto(ProfileDto profileDto);

}