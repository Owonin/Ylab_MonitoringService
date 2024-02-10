package domain.repository.jdbc;

import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.Role;
import domain.model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.ConfigReader;
import util.DBConnectionProvider;
import util.MigrationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
class JdbcMetricRecordRepositoryTest {

    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.1-alpine"
    );

    private static DBConnectionProvider connectionProvider;

    private final JdbcMetricValueRepository metricValueRepository = new JdbcMetricValueRepository();
    private final JdbcMetricRecordRepository metricRecordRepository = new JdbcMetricRecordRepository(connectionProvider);
    private final JdbcUserRepository userRepository = new JdbcUserRepository(connectionProvider);
    private final JdbcMetricRepository metricRepository = new JdbcMetricRepository(connectionProvider);

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        ConfigReader.getInstance();


        String default_schema = ConfigReader.getInstance().getProperty("DEFAULT_SCHEMA");
        MigrationExecutor.execute(connectionProvider, "db.changelog/test_changelog.xml", default_schema);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @Test
    @DisplayName("Получие последний метрики несуществующего пользователя возвращяет null")
    void getLastMetricForUserReturnsEmptyOptional() {
        User user = new User(1000);
        Optional<MetricRecord> lastMetric = metricRecordRepository.findLastMetricForUser(user);

        assertTrue(lastMetric.isEmpty());
    }


    @Test
    @DisplayName("Сохранение метрики сохраняет метрику")
    void saveMetricForUserCreatesNewUserMetricsList() {
        User user = new User(1, "randomUsername", "psw", Set.of(Role.USER));
        user = userRepository.save(user);

        Metric heatingReading = new Metric(1,"heatingReading");
        heatingReading= metricRepository.save(heatingReading);

        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);
        LocalDate localDate = LocalDate.of(2000,12,24);

        MetricRecord expectedMetric = new MetricRecord(1,metrics, localDate, user);


        MetricRecord resultRecord = metricRecordRepository.save(user, expectedMetric);

        assertNotNull(resultRecord);
        assertEquals(resultRecord.getUser(),user);
        assertEquals(resultRecord.getMetrics().get(heatingReading), metrics.get(heatingReading));

    }

    @Test
    @DisplayName("Поиск пользовательских метрик возвращает метрики")
    void testFindMetricRecordByUser() {
        User user = new User(1, "randomUsername2", "psw", Set.of(Role.USER));
        user = userRepository.save(user);

        Metric heatingReading = new Metric(1,"heatingReading2");
        heatingReading = metricRepository.save(heatingReading);

        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);
        LocalDate localDate = LocalDate.of(2000,12,24);
        LocalDate localDate2 = LocalDate.of(2000,10,24);


        MetricRecord expectedMetric = metricRecordRepository.save(user,new MetricRecord(1,metrics, localDate, user));
        MetricRecord expectedMetric2 = metricRecordRepository.save(user,new MetricRecord(1,metrics, localDate2, user));

        List<MetricRecord> resultRecord = metricRecordRepository.findUserMetrics(user);

        assertNotNull(resultRecord); //todo
        assertTrue(resultRecord.contains(expectedMetric));
        assertTrue(resultRecord.contains(expectedMetric2));
    }

    @Test
    @DisplayName("Получие последний метрики возвращяет метрику")
    void getLastMetricForUserReturnsLastMetric() {
        Metric heatingReading = new Metric(1, "heatingReading3");
        heatingReading = metricRepository.save(heatingReading);
        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);

        LocalDate firstLocalDate = LocalDate.of(2000,12,24);
        LocalDate secondLocalDate = LocalDate.now();

        User user = new User(1, "UserTestName", "psw", Set.of(Role.USER));
        user = userRepository.save(user);

        MetricRecord oldMetricRecord = new MetricRecord(1,metrics, firstLocalDate, user);
        MetricRecord newMetricRecord = new MetricRecord(1,metrics, secondLocalDate, user);


        MetricRecord savedMetricRecord = metricRecordRepository.save(user, newMetricRecord);

        Optional<MetricRecord> lastMetric = metricRecordRepository.findLastMetricForUser(user);

        assertTrue(lastMetric.isPresent());
        assertNotEquals(oldMetricRecord, newMetricRecord);
        assertEquals(newMetricRecord,savedMetricRecord);
    }
}