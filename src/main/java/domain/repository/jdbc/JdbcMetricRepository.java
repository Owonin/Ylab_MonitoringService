package domain.repository.jdbc;

import domain.model.Metric;
import domain.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import util.DBConnectionProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс, реализующий взаимодействие с таблицей метрик на основе JDBC.
 */
@Repository
@RequiredArgsConstructor
public class JdbcMetricRepository implements MetricRepository {

    public static final String RETRIEVE_USERS_SQL = "SELECT * FROM private_schema.metrics";
    public static final String FIND_USER_BY_ID_SQL = "SELECT * FROM private_schema.metrics WHERE name = ?";
    public static final String INSERT_USER_SQL = "INSERT INTO private_schema.metrics (name) VALUES (?)";
    private final DBConnectionProvider connectionProvider;

    /**
     * Добавляет новую метрику в репозиторий.
     *
     * @param metric Метрика, которую необходимо добавить.
     * @return Сохраненная метрика, иначе null
     */
    @Override
    public Metric save(Metric metric) {
        try (Connection connection = connectionProvider.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, metric.getName());
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            metric.setId(generatedKeys.getInt(1));
                            connection.commit();
                            return metric;
                        }
                    }
                    connection.commit();
                }
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка соединения с БД " + e.getMessage());
        }
        return null;
    }

    /**
     * Возвращает список всех метрик из репозитория.
     *
     * @return Список всех метрик из репозитория.
     */
    @Override
    public List<Metric> findAllMetrics() {
        List<Metric> metrics = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection()) {

            Statement statement = connection.createStatement();
            try (ResultSet resultSet = statement.executeQuery(RETRIEVE_USERS_SQL)) {
                metrics = ResultSetToMetricsListMapper(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        }

        return metrics;
    }

    /**
     * Возвращает метрику из репозитория по названию.
     *
     * @param name Название метрики
     * @return Метрика
     */
    @Override
    public Optional<Metric> findMetricByName(String name) {
        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_SQL);
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Metric> users = ResultSetToMetricsListMapper(resultSet);
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
     * Преобразует ответ от SQL запроса в список метрик
     *
     * @param resultSet Мнжество параметров ответа SQL запроса
     * @return Список метрик
     * @throws SQLException Ошибка выполнения SQL
     */
    private List<Metric> ResultSetToMetricsListMapper(ResultSet resultSet) throws SQLException {
        ArrayList<Metric> users = new ArrayList<>();
        while (resultSet.next()) {
            int metricId = resultSet.getInt("id");
            String name = resultSet.getString("name");
            users.add(new Metric(metricId, name));
        }
        return users;
    }
}
