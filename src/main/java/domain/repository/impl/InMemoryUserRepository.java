package domain.repository.impl;

import domain.repository.UserRepository;
import domain.model.User;

import java.util.*;

/**
 * Класс, реализующий хранение пользователей в оперативной памяти.
 */
public class InMemoryUserRepository implements UserRepository {

    Set<User> users = new HashSet<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> findUserById(String id) {
        for (User user : users) {
            if (user.getUserId().equals(id)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

}
