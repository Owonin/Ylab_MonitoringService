package service.impl;

import auth.AuthContext;
import domain.exception.NotFoundException;
import domain.model.Role;
import domain.model.User;
import domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.AuthenticationException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private AuthContext mockAuthContext;

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(mockUserRepository, mockAuthContext);
    }

    @Test
    public void testRegistrateUserSuccessfulRegistration() throws AuthenticationException {
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
    public void testRegistrateUserUserAlreadyExists() {
        String username = "existingUser";
        String password = "password";
        Set<Role> roles = Collections.singleton(Role.USER);

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.of(new User("id", username)));

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> userService.registrateUser(username, password, roles));

        verify(mockUserRepository).findUserByUsername(username);
    }

    @Test
    public void testLogin_SuccessfulLogin() throws NotFoundException, AuthenticationException {
        String username = "testUser";
        String password = "testPassword";
        User user = new User("id", username);
        user.setPassword(password);

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.login(username, password));

        verify(mockUserRepository).findUserByUsername(username);
        verify(mockAuthContext).authenticateUser(user);
    }

    @Test
    public void testLoginUserNotFound() {
        String username = "nonExistingUser";
        String password = "password";

        when(mockUserRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.login(username, password));

        verify(mockUserRepository).findUserByUsername(username);
    }

    @Test
    public void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User("id", "user1"),
                new User("id", "user2")
        );

        when(mockUserRepository.findAllUsers()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(mockUsers, result);
    }


}