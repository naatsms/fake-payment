package naatsms.person.mapper;

import naatsms.person.dto.AddressDto;
import naatsms.person.entity.Address;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(source = "address", target = "addressLine")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    Address addressFromDto(AddressDto address);

    @Mapping(source = "addressLine", target = "address")
    AddressDto addressToDto(Address address);

    @InheritConfiguration
    Address updateFromDto(@MappingTarget Address address, AddressDto dto);

    @DeepClone
    Address clone(Address address);

}