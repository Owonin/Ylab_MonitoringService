package domain.repository.jdbc;

import domain.model.Metric;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.ConfigReader;
import util.DBConnectionProvider;
import util.MigrationExecutor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JdbcMetricRepositoryTest {

    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.1-alpine"
    );

    private static DBConnectionProvider connectionProvider;

    private final JdbcMetricRepository metricRepository = new JdbcMetricRepository(connectionProvider);

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        String default_schema = ConfigReader.getInstance().getProperty("DEFAULT_SCHEMA");
        MigrationExecutor.execute(connectionProvider, "db.changelog/test_changelog.xml", default_schema);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Получение всех метрик возвращяет метрики")
    public void testSaveAndShowAllMetrics() {
        Metric metric1 = new Metric(1,"hotWater");
        Metric metric2 = new Metric(2,"coldWater");

        metric1 = metricRepository.save(metric1);
        metric2 = metricRepository.save(metric2);

        List<Metric> allMetrics = metricRepository.findAllMetrics();
        assertNotNull(allMetrics);
        assertTrue(allMetrics.contains(metric1));
        assertTrue(allMetrics.contains(metric2));
    }

    @Test
    @DisplayName("Получение метрики по имени возвращяет метрику")
    public void testSaveAndShowMetricByName() {
        String metricName = "Ну тут то эта метрика точно ест";
        Metric expectedMetric = new Metric(1,metricName);

        expectedMetric = metricRepository.save(expectedMetric);

        Optional<Metric> actualMetric = metricRepository.findMetricByName(metricName);
        assertTrue(actualMetric.isPresent());
        assertEquals(actualMetric.get(),expectedMetric);
    }

    @Test
    @DisplayName("Получение метрики по несуществующему имени возвращяет null")
    public void testSaveAndShowMetricByNameReturnEmptyMetric() {
        String metricName = "Amඞgus";

        Optional<Metric> actualMetric = metricRepository.findMetricByName(metricName);
        assertTrue(actualMetric.isEmpty());
    }
}