package naatsms.person.mapper;

import naatsms.person.dto.AddressDto;
import naatsms.person.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(source = "address", target = "addressLine")
    Address addressFromDto(AddressDto address);

    @Mapping(source = "addressLine", target = "address")
    AddressDto addressToDto(Address address);

}