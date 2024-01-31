package domain.model;

import auth.UserDetails;
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
public class User implements UserDetails {

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     *
     * @param userId Идентификатор пользователя.
     */
    public User(String userId) {
        this.userId = userId;
    }

    /**
     * Конструктор для создания нового пользователя с указанным идентификатором.
     *
     * @param userId   Идентификатор пользователя.
     * @param username Никнейм пользователя.
     */
    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    private String userId;
    private String username;
    private String password;
    private Set<Role> roles;
    private List<MetricRecord> metrics;


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
    public Set<?> getAuthorities() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) && username.equals(user.username) && password.equals(user.password) && roles.equals(user.roles) && Objects.equals(metrics, user.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, roles);
    }
}
