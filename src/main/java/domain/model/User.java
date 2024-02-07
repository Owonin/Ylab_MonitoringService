package domain.model;

import auth.UserDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Класс, представляющий пользователя системы.
 */
@Getter
@Setter
@EqualsAndHashCode
public class User implements UserDetails {

    private Integer userId;
    private String username;
    private String password;
    private Set<Role> roles;

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     */
    public User(){
    }

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     *
     * @param userId Идентификатор пользователя.
     */
    public User(int userId) {
        this.userId = userId;
    }

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     *
     * @param userId   Идентификатор пользователя.
     * @param username Никнейм пользователя.
     */
    public User(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     *
     * @param userId   Идентификатор пользователя.
     * @param username Никнейм пользователя.
     * @param password Пароль пользователя
     */
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     *
     * @param userId   Идентификатор пользователя.
     * @param username Никнейм пользователя.
     * @param password Пароль пользователя
     * @param roles    Роли пользователя
     */
    public User(int userId, String username, String password, Set<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return Имя пользователя.
     */
    @Override
    public String getUsername() {
        return username;
    }


    /**
     * Возвращает набор прав (ролей) пользователя.
     *
     * @return Набор прав (ролей) пользователя.
     */
    @Override
    public Set<Role> getAuthorities() {
        return roles;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return Пароль пользователя.
     */
    @Override
    public String getPassword() {
        return password;
    }
}
