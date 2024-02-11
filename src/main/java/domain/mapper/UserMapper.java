package domain.mapper;

import domain.model.Role;
import domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import out.dto.UserDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userDtoToUser(UserDto userDto);


    UserDto userToUserDto(User user);

    default Set<Role> mapRoles(Set<String> roles) {
        Set<Role> roleSet = new HashSet<>();
        for (String roleName : roles) {
            Role role = Role.getById(Integer.parseInt(roleName));
            roleSet.add(role);
        }
        return roleSet;
    }

}
