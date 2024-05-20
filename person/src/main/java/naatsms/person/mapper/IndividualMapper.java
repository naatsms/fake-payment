package naatsms.person.mapper;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ProfileMapper.class)
public interface IndividualMapper {

    IndividualMapper INSTANCE = Mappers.getMapper(IndividualMapper.class);

    Individual individualFromDto(IndividualDto profileDto);

    IndividualDto dtoFromIndividual(Individual entity);

    Individual updateFromDto(@MappingTarget Individual individual, IndividualDto dto);

}