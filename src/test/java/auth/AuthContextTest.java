package auth;

import com.example.auth.AuthContext;
import com.example.auth.UserDetails;
import com.example.domain.exception.MyAuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthContextTest {

    @Mock
    private UserDetails mockUser;

    @InjectMocks
    private AuthContext authContext;

    @BeforeEach
    void setUp() {
        authContext.logoutUser();
    }

    @Test
    @DisplayName("Когда пользователь не аутентифицирован, возвращает false")
    public void testIsUserAuthenticated_WhenUserIsNotAuthenticated_ReturnsFalse() {
        assertFalse(authContext.isUserAuthenticated());
    }

    @Test
    @DisplayName("Когда пользователь аутентифицирован, возвращает true")
    public void testAuthenticateUser_SuccessfulAuthentication() throws AuthenticationException {

        UserDetails result = authContext.authenticateUser(mockUser);

        assertEquals(mockUser, result);
        assertTrue(authContext.isUserAuthenticated());
    }

    @Test
    @DisplayName("Аутентифицирование пользователя, когда пользователь уже аутентифицирован возвращяет ошибку")
    public void testAuthenticateUser_UserAlreadyAuthenticated_ThrowsException() {
        when(mockUser.getUsername()).thenReturn("testUser");

        Assertions.assertDoesNotThrow(() -> authContext.authenticateUser(mockUser));
        MyAuthenticationException exception = assertThrows(MyAuthenticationException.class,
                () -> authContext.authenticateUser(mockUser));

        assertEquals("Пользователь testUser уже аутентифицирован", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка аутентифицировании пользователя, когда пользователь вышел возвращяет false")
    public void testLogoutUser_UserIsAuthenticated_LogsOutUser() throws AuthenticationException {
        authContext.authenticateUser(mockUser);

        authContext.logoutUser();

        assertFalse(authContext.isUserAuthenticated());
    }

    @Test
    @DisplayName("Возвращяет пользователя, когда пользователь аутентифицированан")
    public void testGetCurrentUsername_UserIsAuthenticated_ReturnsUsername() throws AuthenticationException {
        when(mockUser.getUsername()).thenReturn("testUser");

        authContext.authenticateUser(mockUser);

        String result = authContext.getCurrentUsername();

        assertEquals("testUser", result);
    }
}