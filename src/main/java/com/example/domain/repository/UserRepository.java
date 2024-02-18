package com.example.domain.repository;

import com.example.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий операции для работы с пользователями в репозитории.
 */
@Repository
public interface UserRepository {

    /**
     * Возвращает список всех пользователей из репозитория.
     *
     * @return Список всех пользователей из репозитория.
     */
    List<User> findAllUsers();

    /**
     * Сохраняет пользователя в репозиторий.
     *
     * @param user Пользователь, который должен быть сохранен.
     * @return Опциональный объект, содержащий пользователя.
     */
    User save(User user);

    /**
     * Находит пользователя в репозитории по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Опциональный объект, содержащий пользователя.
     */
    Optional<User> findUserById(int id);

    /**
     * Находит пользователя в репозитории по его имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Опциональный объект, содержащий пользователя.
     */
    Optional<User> findUserByUsername(String username);
}