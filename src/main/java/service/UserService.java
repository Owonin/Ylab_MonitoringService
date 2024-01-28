package service;

import domain.exception.NotFoundException;
import domain.model.Role;
import domain.model.User;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс, определяющий операции для работы с пользователями.
 * Предоставляет методы для регистрации нового пользователя и входа в систему.
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя с указанным именем, паролем и ролями.
     *
     * @param username Имя нового пользователя.
     * @param password Пароль нового пользователя.
     * @param roles    Множество ролей нового пользователя.
     * @throws AuthenticationException Если не удалось выполнить регистрацию пользователя.
     */
    void registrateUser(String username, String password, Set<Role> roles) throws AuthenticationException;

    /**
     * Выполняет вход в систему для пользователя с указанным именем и паролем.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @throws NotFoundException       Если пользователь с указанным именем не найден.
     * @throws AuthenticationException Если не удалось выполнить вход в систему.
     */
    void login(String username, String password) throws NotFoundException, AuthenticationException;

    /**
     * Выполняет поиск всех пользователей в репозитории
     *
     * @return Список всех пользователей.
     */
    List<User> getAllUsers();
}