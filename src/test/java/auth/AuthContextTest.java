package auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthContextTest {

    @Mock
    private UserDetails mockUser;

    private AuthContext authContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authContext = new AuthContext();
    }

    @Test
    public void testIsUserAuthenticated_WhenUserIsNotAuthenticated_ReturnsFalse() {
        assertFalse(authContext.isUserAuthenticated());
    }

    @Test
    public void testAuthenticateUser_SuccessfulAuthentication() throws AuthenticationException {
        when(mockUser.getUsername()).thenReturn("testUser");

        UserDetails result = authContext.authenticateUser(mockUser);

        assertEquals(mockUser, result);
        assertTrue(authContext.isUserAuthenticated());
    }

    @Test
    public void testAuthenticateUser_UserAlreadyAuthenticated_ThrowsException() {
        when(mockUser.getUsername()).thenReturn("testUser");

        Assertions.assertDoesNotThrow(() -> authContext.authenticateUser(mockUser));
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authContext.authenticateUser(mockUser));

        assertEquals("Пользователь testUser уже аутентифицирован", exception.getMessage());
    }

    @Test
    public void testLogoutUser_UserIsAuthenticated_LogsOutUser() throws AuthenticationException {
        authContext.authenticateUser(mockUser);

        authContext.logoutUser();

        assertFalse(authContext.isUserAuthenticated());
    }

    @Test
    public void testGetCurrentUsernameUserIsAuthenticatedReturnsUsername() throws AuthenticationException {
        when(mockUser.getUsername()).thenReturn("testUser");

        authContext.authenticateUser(mockUser);

        String result = authContext.getCurrentUsername();

        assertEquals("testUser", result);
    }

}