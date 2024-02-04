package domain.repository.jdbc;

import domain.exception.UserNotFoundException;
import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.User;
import domain.repository.MetricRecordRepository;
import domain.repository.UserRepository;
import util.DBConnectionProvider;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Класс, реализующий взаимодействие с таблицей записи метрик на основе JDBC.
 */
public class JdbcMetricRecordRepository implements MetricRecordRepository {

    public static final String INSERT_INTO_METRICS_RECORD = "INSERT INTO private_schema.metric_records " +
            "(metric_date, user_id) VALUES (?, ?)";
    public static final String SELECT_METRICS_DATA = "SELECT metrics.id, metrics.name, metric_values.value " +
            "FROM private_schema.metric_values JOIN private_schema.metrics on metric_values.metric_id = metrics.id WHERE metric_record_id = ?";
    public static final String INSERT_INTO_METRIC_VALUES = "INSERT INTO private_schema.metric_values " +
            "(value, metric_record_id, metric_id) VALUES (?, ?, ?)";
    public static final String SELECT_LAST_METRIC_DATA = "SELECT * FROM private_schema.metric_records " +
            "WHERE user_id = ? ORDER BY metric_date DESC LIMIT 1";
    public static final String SELECT_FROM_METRIC_RECORDS_WHERE_USER_ID = "SELECT * " +
            "FROM private_schema.metric_records WHERE user_id = ?";
    public static final String SELECT_FROM_METRIC_RECORDS = "SELECT * FROM private_schema.metric_records";
    private final DBConnectionProvider connectionProvider;

    /**
     * Конструктор репозитория
     *
     * @param connectionProvider Данные о присоединении к БД
     */
    public JdbcMetricRecordRepository(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Возвращает последнюю запись метрик для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашивается последняя запись метрик.
     * @return Опциональный объект, содержащий последнюю запись метрик пользователя.
     */
    @Override
    public Optional<MetricRecord> findLastMetricForUser(User user) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SELECT_LAST_METRIC_DATA)) {
            statement.setInt(1, user.getUserId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToMetricRecord(connection, resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает список записей метрик для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашиваются записи метрик.
     * @return Список записей метрик пользователя.
     */
    @Override
    public List<MetricRecord> findUserMetrics(User user) {
        List<MetricRecord> metrics = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SELECT_FROM_METRIC_RECORDS_WHERE_USER_ID)) {
            statement.setInt(1, user.getUserId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    metrics.add(mapResultSetToMetricRecord(connection, resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        return metrics;
    }

    /**
     * Выполняет транзакцию записи метрик для указанного пользователя.
     *
     * @param user   Пользователь, для которого сохраняется запись метрик.
     * @param metric Запись метрик, которую необходимо сохранить.
     * @return Запись метрики, если запись метрик успешно сохранена, иначе null.
     */
    @Override
    public MetricRecord save(User user, MetricRecord metric) {
        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            MetricRecord savedMetricRecord = saveToMetricRecordTable(connection, metric, user);

            if (savedMetricRecord != null) {
                connection.commit();
            } else {
                connection.rollback();
            }
            return savedMetricRecord;
        } catch (SQLException e) {
            System.err.println("Ошибка во время выполнения SQL " + e.getMessage());
        }

        return null;
    }

    /**
     * Возвращает список всех записей метрик из репозитория.
     *
     * @return Список всех записей метрик из репозитория.
     */
    @Override
    public List<MetricRecord> getAllMetrics() {
        List<MetricRecord> metrics = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_FROM_METRIC_RECORDS)) {
            while (resultSet.next()) {
                metrics.add(mapResultSetToMetricRecord(connection, resultSet));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        return metrics;
    }

    /**
     * Преобразует объект resultRest в запись метрики
     *
     * @param connection Соединение с БД
     * @param resultSet  Множество результата ответа SQL запроса
     * @return Запись метрики
     * @throws SQLException          Ошибка SQL запроса
     * @throws UserNotFoundException Ошибка нахождения пользователя
     */
    private MetricRecord mapResultSetToMetricRecord(Connection connection, ResultSet resultSet) throws SQLException, UserNotFoundException {
        int id = resultSet.getInt("id");
        Map<Metric, Integer> metrics = getMetricValues(connection, id);
        LocalDate metricDate = resultSet.getDate("metric_date").toLocalDate();
        int userId = resultSet.getInt("user_id");

        UserRepository userRepository = new JdbcUserRepository(connectionProvider);
         User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User is not found"));

        return new MetricRecord(id, metrics, metricDate, user);
    }

    /**
     * Сохранение значений записей метрик в БД
     *
     * @param connection Соединение с БД
     * @param metrics    Запись метрик
     */
    private void saveMetricValue(Connection connection, MetricRecord metrics) throws SQLException {

        for (Metric metric : metrics.getMetrics().keySet()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    INSERT_INTO_METRIC_VALUES)) {
                statement.setInt(1, metrics.getMetrics().get(metric));
                statement.setInt(2, metrics.getId());
                statement.setInt(3, metric.getId());

                statement.executeUpdate();
            }
        }

    }

    /**
     * Получение значений метрик из базы данных по ключу записи метрик
     *
     * @param connection     Соединение с БД
     * @param metricRecordId ID значений метрик
     * @return Данные о значениях метрик в формате ключ-значение
     */
    private Map<Metric, Integer> getMetricValues(Connection connection, int metricRecordId) {
        Map<Metric, Integer> metricValues = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_METRICS_DATA)) {
            statement.setInt(1, metricRecordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int metricId = resultSet.getInt("id");
                    String metricName = resultSet.getString("name");
                    int value = resultSet.getInt("value");
                    Metric metric = new Metric(metricId, metricName);
                    metricValues.put(metric, value);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
        }

        return metricValues;
    }

    /**
     * Сохранение записи метрики в БД.
     *
     * @param connection Соединение с БД
     * @param metric Запись о метрике
     * @param user Пользователь создавший запись
     * @return  Запись о метрике, если запись сохранена, иначе null
     * @throws SQLException Ошибка выполнения SQL
     */
    private MetricRecord saveToMetricRecordTable(Connection connection, MetricRecord metric, User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                INSERT_INTO_METRICS_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            statement.setDate(1, Date.valueOf(metric.getMetricDate()));
            statement.setInt(2, user.getUserId());

            if (statement.executeUpdate() > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    metric.setId(generatedKeys.getInt(1));
                    saveMetricValue(connection, metric);
                    return metric;
                }
            }
        }
        return null;
    }
}
