package service.impl;

import auth.AuthContext;
import domain.exception.NotFoundException;
import domain.model.Role;
import domain.model.User;
import domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private AuthContext mockAuthContext;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Регестрирует нового пользователя")
    public void testRegistrateUserSuccessfulRegistration() {
        String username = "testUser";
        String password = "testPassword";
        Set<Role> roles = Collections.singleton(Role.USER);

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.empty());
        doNothing().when(mockUserRepository).save(any(User.class));

        assertDoesNotThrow(() -> userService.registrateUser(username, password, roles));

        verify(mockUserRepository).findUserByUsername(username);
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Регестрация нового пользователя вызывает ошибку аунтификации")
    public void testRegistrateUserUserAlreadyExists() {
        String username = "existingUser";
        String password = "password";
        Set<Role> roles = Collections.singleton(Role.USER);

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.of(new User(1, username)));

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> userService.registrateUser(username, password, roles));

        verify(mockUserRepository).findUserByUsername(username);
    }

    @Test
    @DisplayName("Успешный вход в аккаунт")
    public void testLogin_SuccessfulLogin() throws AuthenticationException {
        String username = "testUser";
        String password = "testPassword";
        User user = new User(1, username);
        user.setPassword(password);

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.login(username, password));

        verify(mockUserRepository).findUserByUsername(username);
        verify(mockAuthContext).authenticateUser(user);
    }

    @Test
    @DisplayName("Вход в аккаунт вызывает ошибку")
    public void testLoginUserNotFound() {
        String username = "nonExistingUser";
        String password = "password";

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.login(username, password));

        verify(mockUserRepository).findUserByUsername(username);
    }

    @Test
    @DisplayName("Получить список всех пользователей")
    public void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User(1, "user1"),
                new User(1, "user2")
        );

        when(mockUserRepository.findAllUsers()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(mockUsers, result);
    }


}