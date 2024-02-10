package domain.mapper;

import domain.model.Role;
import domain.model.User;
import org.junit.jupiter.api.Test;
import out.dto.UserDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    }
}