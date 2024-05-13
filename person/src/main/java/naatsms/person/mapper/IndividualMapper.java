package naatsms.person.mapper;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IndividualMapper {

    IndividualMapper INSTANCE = Mappers.getMapper(IndividualMapper.class);

    Individual individualFromDto(IndividualDto profileDto);

}