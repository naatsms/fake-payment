package naatsms.person.mapper;

import naatsms.person.dto.IndividualDto;
import naatsms.person.entity.Individual;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ProfileMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IndividualMapper {

    IndividualMapper INSTANCE = Mappers.getMapper(IndividualMapper.class);

    @Mapping(target = "profileId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Individual individualFromDto(IndividualDto dto);

    IndividualDto dtoFromIndividual(Individual entity);

    @InheritConfiguration
    Individual updateFromDto(@MappingTarget Individual individual, IndividualDto dto);

    @DeepClone
    Individual clone(Individual individual);

}