package com.example.service.impl;


import com.example.aop.annotations.Loggable;
import com.example.auth.AuthContext;
import com.example.domain.exception.MyAuthenticationException;
import com.example.domain.exception.NotFoundException;
import com.example.domain.exception.UserNotFoundException;
import com.example.domain.model.Role;
import com.example.domain.model.User;
import com.example.domain.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Set;

/**
 * Класс, представляющий реализацию сервиса работы с пользователями.
 */
@Service
@Loggable
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final String USER_IS_ALREADY_PRESENT_ERROR_MESSAGE = "User with this username is already present";

    private final UserRepository userRepository;

    /**
     * Регистрирует нового пользователя с указанным именем, паролем и ролями.
     *
     * @param username Имя нового пользователя.
     * @param password Пароль нового пользователя.
     * @param roles    Множество ролей нового пользователя.
     */
    @Override
    public void registrateUser(String username, String password, Set<Role> roles) {

        if (userRepository.findUserByUsername(username).isPresent()) {
            throw new MyAuthenticationException(USER_IS_ALREADY_PRESENT_ERROR_MESSAGE);
        } else {
            User user = new User();
            user.setPassword(password);
            user.setUsername(username);
            user.setRoles(roles);

            userRepository.save(user);
        }
    }

    /**
     * Выполняет вход в систему для пользователя с указанным именем и паролем.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @throws NotFoundException       Если пользователь с указанным именем не найден.
     * @throws AuthenticationException Если не удалось выполнить вход в систему.
     */
    @Override
    public void login(String username, String password, AuthContext authContext) throws NotFoundException, MyAuthenticationException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (password.equals(user.getPassword())) {
            authContext.authenticateUser(user);
        }
    }

    /**
     * Выполняет поиск всех пользователей в репозитории
     *
     * @return Список всех пользователей.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }
}
