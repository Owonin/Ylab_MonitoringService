package domain.repository.jdbc;

import domain.model.Role;
import domain.model.User;
import domain.repository.UserRepository;
import util.DBConnectionProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс, реализующий взаимодействие с таблицей пользователей на основе JDBC.
 */
public class JdbcUserRepository implements UserRepository {

    public static final String INSERT_USER_SQL = "INSERT INTO private_schema.users (username, password) VALUES (?, ?)";
    public static final String FIND_USER_BY_ID_SQL = "SELECT * FROM private_schema.users WHERE id = ?";
    public static final String FIND_USER_BY_NAME = "SELECT * FROM private_schema.users WHERE username = ?";
    public static final String FIND_ROLES_BY_USER_ID = "SELECT role_id FROM private_schema.user_roles WHERE user_id = ?";
    public static final String INSERT_INTO_USER_ROLES = "INSERT INTO private_schema.user_roles (user_id, role_id) VALUES (?, ?)";
    public static final String RETRIEVE_USERS_SQL = "SELECT * FROM private_schema.users";
    private final DBConnectionProvider connectionProvider;

    public JdbcUserRepository(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Возвращает список всех пользователей из репозитория.
     *
     * @return Список всех пользователей из репозитория.
     */
    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(RETRIEVE_USERS_SQL);
            users = ResultSetToUserListMapper(resultSet);
        } catch (SQLException e) {
            System.out.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        }

        return users;
    }

    /**
     * Выполняет транзакцию записи пользователя в базу данных.
     *
     * @param user Пользователь, который должен быть сохранен.
     * @return Опциональный объект, содержащий пользователя.
     */
    @Override
    public User save(User user) {
        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try {
                User resultUser = saveUser(user, connection);

                saveUserRoles(connection, user);

                connection.commit();
                return resultUser;
            } catch (SQLException e) {
                System.err.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
                connection.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к БД" + e.getMessage());
        }
        return null;
    }

    /**
     * Выполняет SQL запрос на сохранение ролей пользователя в БД
     *
     * @param user       Данные пользователья
     * @param connection Соединение с БД
     * @throws SQLException Ошибка выполнения SQL запроса
     */
    private void saveUserRoles(Connection connection, User user) throws SQLException {
        for (Role role : user.getRoles()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_USER_ROLES, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, user.getUserId());
                preparedStatement.setInt(2, role.ordinal() + 1);
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Выполняет SQL запрос на сохранение пользователя в БД
     *
     * @param user       Данные пользователья
     * @param connection Соединение с БД
     * @return Данные пользователя, если сохранен, иначе null
     * @throws SQLException Ошибка выполнения SQL запроса
     */
    private User saveUser(User user, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                        return user;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Находит пользователя в репозитории по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Опциональный объект, содержащий пользователя.
     */
    @Override
    public Optional<User> findUserById(int id) { //todo roles adding to user
        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_SQL);
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<User> users = ResultSetToUserListMapper(resultSet);
                if (!users.isEmpty()) {
                    return Optional.of(users.get(0));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Находит пользователя в репозитории по его имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Опциональный объект, содержащий пользователя.
     */
    @Override
    public Optional<User> findUserByUsername(String username) {
        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_NAME);
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                List<User> users = ResultSetToUserListMapper(resultSet);
                if (users.isEmpty())
                    return Optional.empty();
                User user = users.get(0);
                user.setRoles(loadRoles(connection, user.getUserId()));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        }

        return Optional.empty();
    }


    /**
     * Получене множества ролей
     *
     * @param connection    Соединение с БД
     * @param userId        Индификатор пользователя
     * @return              Множество ролей
     * @throws SQLException Ошибка выполнения SQL
     */
    private Set<Role> loadRoles(Connection connection, int userId) throws SQLException {
        List<Integer> roleIds = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ROLES_BY_USER_ID)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    roleIds.add(resultSet.getInt("role_id"));
                }
            }
            return roleIds.stream()
                    .map(Role::getById)
                    .collect(Collectors.toSet());
        }
    }


    /**
     * Преобразует ответ от SQL запроса в список пользователей
     *
     * @param resultSet Мнжество параметров ответа SQL запроса
     * @return Список пользователей
     * @throws SQLException Ошибка выполнения SQL
     */
    private List<User> ResultSetToUserListMapper(ResultSet resultSet) throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        while (resultSet.next()) {
            int userId = resultSet.getInt("id");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            users.add(new User(userId, username, password));
        }

        return users;
    }
}
