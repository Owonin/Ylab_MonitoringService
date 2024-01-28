package service.impl;

import domain.model.Metric;
import domain.repository.MetricRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import service.MetricService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MetricServiceImplTest {
    @Mock
    private static MetricRepository mockRepository;
    private static MetricService metricService;

    @BeforeAll
    static void setUp() {
        mockRepository = mock(MetricRepository.class);
        metricService = new MetricServiceImpl(mockRepository);
    }

    @Test
    public void testGetAllMetric() {
        List<Metric> mockMetrics = new ArrayList<>();
        mockMetrics.add(new Metric("id", "hotwatr"));
        mockMetrics.add(new Metric("id2", "coldWater"));

        when(mockRepository.allMetrics()).thenReturn(mockMetrics);

        List<Metric> result = metricService.getAllMetric();

        assertEquals(mockMetrics, result);
    }

    @Test
    public void testGetMetricsSize() {
        List<Metric> mockMetrics = new ArrayList<>();
        mockMetrics.add(new Metric("id", "hotwatr"));
        mockMetrics.add(new Metric("id2", "coldWater"));

        when(mockRepository.allMetrics()).thenReturn(mockMetrics);

        int result = metricService.getMetricsSize();

        assertEquals(mockMetrics.size(), result);
    }

    @Test
    public void testAddNewMetric() {

        metricService.addNewMetric("New Metric");

        verify(mockRepository).addMetric(any(Metric.class));
    }
}