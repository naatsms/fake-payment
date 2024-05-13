package naatsms.person.mapper;

import naatsms.person.dto.AddressDto;
import naatsms.person.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    Address addressFromDto(AddressDto address);

}