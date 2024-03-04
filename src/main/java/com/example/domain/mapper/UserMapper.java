package com.example.domain.mapper;

import com.example.domain.model.Role;
import com.example.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.example.out.dto.UserDto;

import java.util.HashSet;
import java.util.Set;

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
