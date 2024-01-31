package auth;

import java.util.Set;


/**
 * Интерфейс, представляющий детали пользователя для системы авторизации.
 */
public interface UserDetails {

    /**
     * Возвращает имя пользователя.
     *
     * @return Имя пользователя.
     */
    String getUsername();

    /**
     * Возвращает набор прав (ролей) пользователя.
     *
     * @return Набор прав (ролей) пользователя.
     */
    Set<?> getAuthorities();

    /**
     * Возвращает пароль пользователя.
     *
     * @return Пароль пользователя.
     */
    String getPassword();
}