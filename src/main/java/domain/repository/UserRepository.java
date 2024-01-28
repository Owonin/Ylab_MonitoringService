package domain.repository;

import domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий операции для работы с пользователями в репозитории.
 */
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
     */
    void save(User user);

    /**
     * Находит пользователя в репозитории по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Опциональный объект, содержащий пользователя.
     */
    Optional<User> findUserById(String id);

    /**
     * Находит пользователя в репозитории по его имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Опциональный объект, содержащий пользователя.
     */
    Optional<User> findUserByUsername(String username);
}