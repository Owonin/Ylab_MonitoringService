package domain.repository.impl;

import domain.repository.impl.InMemoryMetricRepository;
import domain.model.Metric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMetricRepositoryTest {

    private InMemoryMetricRepository metricRepository;

    @BeforeEach
    public void setUp() {
        metricRepository = new InMemoryMetricRepository();
    }

    @Test
    public void testAddMetric() {
        Metric metric = new Metric("id","hotWater");

        assertTrue(metricRepository.addMetric(metric));

        assertEquals(1, metricRepository.allMetrics().size());
        assertTrue(metricRepository.allMetrics().contains(metric));
    }

    @Test
    public void testAllMetrics() {
        Metric metric1 = new Metric("id", "hotWater");
        Metric metric2 = new Metric("id2", "coldWater");

        metricRepository.addMetric(metric1);
        metricRepository.addMetric(metric2);

        List<Metric> allMetrics = metricRepository.allMetrics();
        assertEquals(2, allMetrics.size());
        assertTrue(allMetrics.contains(metric1));
        assertTrue(allMetrics.contains(metric2));
    }

}