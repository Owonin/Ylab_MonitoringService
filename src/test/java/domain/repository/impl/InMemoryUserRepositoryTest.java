package domain.repository.impl;

import domain.repository.impl.InMemoryUserRepository;
import domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    public void testSaveAndFindUserById() {
        User user = new User("1", "User1");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findUserById("1");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    public void testSaveAndFindUserByUsername() {
        String username = "User1";
        User user = new User("1", username);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findUserByUsername(username);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    public void testFindUserByIdNotFound() {
        Optional<User> foundUser = userRepository.findUserById("nonexistent");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void testFindUserByUsernameNotFound() {
        Optional<User> foundUser = userRepository.findUserByUsername("nonexistent");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User("1", "User1");
        User user2 = new User("2", "User2");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> allUsers = userRepository.findAllUsers();

        assertEquals(2, allUsers.size());
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

}