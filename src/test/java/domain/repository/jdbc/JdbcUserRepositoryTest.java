package domain.repository.jdbc;

import domain.model.Role;
import domain.model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.DBConnectionProvider;
import util.MigrationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JdbcUserRepositoryTest {

    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.1-alpine"
    );

    private static DBConnectionProvider connectionProvider;

    private final JdbcUserRepository repository = new JdbcUserRepository(connectionProvider);


    @BeforeAll
    static void beforeAll() {
        postgres.start();

        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        MigrationExecutor.execute(connectionProvider, "db.changelog/test_changelog.xml");
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    public void testFindAllUsersReturnsUserList() {
        User user1 = new User(0, "testUser1", "psw", Set.of(Role.USER));
        User user2 = new User(0, "testUser2", "psw", Set.of(Role.ADMIN));

        user1 = repository.save(user1);
        user2 = repository.save(user2);

        List<User> allUsers = repository.findAllUsers();

        assertNotNull(allUsers);
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void testSaveAndFindUserById() {
        User user = new User(1, "testUser3", "psw", Set.of(Role.USER));

        user = repository.save(user);

        Optional<User> foundUser = repository.findUserById(user.getUserId());

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    public void testSaveAndFindUserByUsername() {
        String username = "testUser4";
        User user = new User(1, username, "psw", Set.of(Role.USER));

        repository.save(user);


        Optional<User> foundUser = repository.findUserByUsername(username);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getPassword(), foundUser.get().getPassword());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
    }

    @Test
    public void testFindUserByIdNotFound() {
        Optional<User> foundUser = repository.findUserById(1010);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void testFindUserByUsernameNotFound() {
        Optional<User> foundUser = repository.findUserByUsername("ThisUsernameIsNotPresented");
        assertTrue(foundUser.isEmpty());
    }
}