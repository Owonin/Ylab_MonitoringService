package domain.repository.impl;

import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMetricRecordRepositoryTest {

    private InMemoryMetricRecordRepository metricRepository;

    @BeforeEach
    void setUp() {
        metricRepository = new InMemoryMetricRecordRepository();
    }

    @Test
    void getLastMetricForUserReturnsEmptyOptional() {
        User user = new User("testUser");
        Optional<MetricRecord> lastMetric = metricRepository.getLastMetricForUser(user);

        assertTrue(lastMetric.isEmpty());
    }

    @Test
    void getLastMetricForUserReturnsLastMetric() {
        Metric heatingReading = new Metric("id", "heatingReading");
        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);
        LocalDate firstLocalDate = LocalDate.now().minusMonths(1);
        LocalDate secondLocalDate = LocalDate.now();
        User user = new User("testUser");

        MetricRecord metric1 = new MetricRecord(metrics, firstLocalDate, user);
        MetricRecord metric2 = new MetricRecord(metrics, secondLocalDate, user);


        metricRepository.saveMetricForUser(user, metric1);
        metricRepository.saveMetricForUser(user, metric2);

        Optional<MetricRecord> lastMetric = metricRepository.getLastMetricForUser(user);

        assertTrue(lastMetric.isPresent());
        assertEquals(metric2, lastMetric.get());
    }

    @Test
    void getUserMetricsWithNoMetricsReturnsEmptyList() {
        User user = new User("testUser");
        List<MetricRecord> userMetrics = metricRepository.getUserMetrics(user);

        assertTrue(userMetrics.isEmpty());
    }

    @Test
    void getUserMetricsReturnsMetricsList() {
        Metric heatingReading = new Metric("id", "heatingReading");
        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);
        LocalDate firstLocalDate = LocalDate.now().minusMonths(1);
        LocalDate secondLocalDate = LocalDate.now();
        User user = new User("testUser");

        MetricRecord metric1 = new MetricRecord(metrics, firstLocalDate, user);
        MetricRecord metric2 = new MetricRecord(metrics, secondLocalDate, user);


        metricRepository.saveMetricForUser(user, metric1);
        metricRepository.saveMetricForUser(user, metric2);

        List<MetricRecord> userMetrics = metricRepository.getUserMetrics(user);

        assertFalse(userMetrics.isEmpty());
        assertEquals(2, userMetrics.size());
        assertTrue(userMetrics.contains(metric1));
        assertTrue(userMetrics.contains(metric2));
    }

    @Test
    void saveMetricForUserCreatesNewUserMetricsList() {
        Metric heatingReading = new Metric("id", "heatingReading");
        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);
        LocalDate localDate = LocalDate.now();
        User user = new User("testUser");

        MetricRecord metric = new MetricRecord(metrics, localDate, user);


        boolean result = metricRepository.saveMetricForUser(user, metric);

        assertTrue(result);
        assertTrue(metricRepository.getUserMetrics(user).contains(metric));
    }

    @Test
    void saveMetricForUserAddsMetricToExistingMetricsList() {
        Metric heatingReading = new Metric("id", "heatingReading");
        Map<Metric, Integer> metrics = Map.of(heatingReading, 100);
        LocalDate firstLocalDate = LocalDate.now().minusMonths(1);
        LocalDate secondLocalDate = LocalDate.now();
        User user = new User("testUser");

        MetricRecord metric1 = new MetricRecord(metrics, firstLocalDate, user);
        MetricRecord metric2 = new MetricRecord(metrics, secondLocalDate, user);


        metricRepository.saveMetricForUser(user, metric1);

        boolean result = metricRepository.saveMetricForUser(user, metric2);

        assertTrue(result);
        assertTrue(metricRepository.getUserMetrics(user).contains(metric1));
        assertTrue(metricRepository.getUserMetrics(user).contains(metric2));
    }

}