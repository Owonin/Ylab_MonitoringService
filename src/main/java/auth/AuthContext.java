package auth;

import domain.model.Role;

import javax.naming.AuthenticationException;
import java.util.Set;

/**
 * Класс, представляющий контекст аутентификации пользователя.
 */
public class AuthContext {

    /**
     * Сообщение для выводе сообщения об ошибке аутентифиции
     */
    private final String ERROR_MESSAGE = "Пользователь %s уже аутентифицирован";

    /**
     * Данные о текущем авторизованном пользователе
     */
    private UserDetails currentUser;

    /**
     * Проверяет, аутентифицирован ли текущий пользователь.
     *
     * @return true, если пользователь аутентифицирован, иначе false.
     */
    public boolean isUserAuthenticated() {
        return currentUser != null;
    }

    /**
     * Аутентифицирует пользователя.
     *
     * @param user Пользователь для аутентификации.
     * @return Детали аутентифицированного пользователя.
     * @throws AuthenticationException Если пользователь уже аутентифицирован.
     */
    public UserDetails authenticateUser(UserDetails user) throws AuthenticationException {
        if (isUserAuthenticated()) {
            throwAuthenticateErrorWithFormatMessage();
        }
        currentUser = user;

        return currentUser;
    }

    /**
     * Вызывает ошибку аутентификации.
     */
    private void throwAuthenticateErrorWithFormatMessage() throws AuthenticationException {
        throw new AuthenticationException(String.format(ERROR_MESSAGE, currentUser.getUsername()));
    }

    /**
     * Выход пользователя из системы.
     */
    public void logoutUser() {
        currentUser = null;
    }

    /**
     * Возвращает роли текущего аутентифицированного пользователя.
     *
     * @return Набор ролей текущего пользователя.
     * @throws AuthenticationException Если пользователь не аутентифицирован.
     */
    public Set<Role> getCurrentUserRoles() throws AuthenticationException {
        if (!isUserAuthenticated()) {
            throwAuthenticateErrorWithFormatMessage();
        }

        return currentUser.getAuthorities();
    }

    /**
     * Возвращает имя текущего аутентифицированного пользователя.
     *
     * @return Имя текущего пользователя.
     * @throws AuthenticationException Если пользователь не аутентифицирован.
     */
    public String getCurrentUsername() throws AuthenticationException {
        if (!isUserAuthenticated()) {
            throwAuthenticateErrorWithFormatMessage();
        }

        return currentUser.getUsername();
    }

}
