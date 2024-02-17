package domain.mapper;

import domain.model.Role;
import domain.model.User;
import org.junit.jupiter.api.Test;
import out.dto.UserDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    @Test
    void userDtoToUser() {
        //given
        User user = new User( 1, "Username","psw", Set.of(Role.USER));

        //when
        UserDto userDto = UserMapper.INSTANCE.userToUserDto( user );

        //then
        assertNotNull( userDto );
        assertEquals(user.getPassword(),userDto.getPassword());
    }

    @Test
    void userToUserDto() {
        //given
        UserDto userDto = new UserDto();

        userDto.setUserId(1);
        userDto.setPassword("psw");
        userDto.setRoles(Set.of(Role.USER.name()));
        userDto.setUsername("username");

        //when
        User user = UserMapper.INSTANCE.userDtoToUser( userDto );

        //then
        assertNotNull( userDto );
        assertEquals(user.getPassword(),userDto.getPassword());
        assertEquals(user.getUsername(),userDto.getUsername());
    }
}