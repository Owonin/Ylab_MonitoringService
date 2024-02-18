package com.example.service.impl;

import com.example.domain.model.Metric;
import com.example.domain.repository.MetricRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricServiceImplTest {

    @Mock
    private MetricRepository mockRepository;

    @InjectMocks
    private MetricServiceImpl metricService;

    @Test
    @DisplayName("Возвращяет все метрики")
    public void testGetAllMetric() {
        List<Metric> mockMetrics = new ArrayList<>();
        mockMetrics.add(new Metric(1, "hotwatr"));
        mockMetrics.add(new Metric(2, "coldWater"));

        when(mockRepository.findAllMetrics()).thenReturn(mockMetrics);

        List<Metric> result = metricService.getAllMetric();

        assertEquals(mockMetrics, result);
    }

    @Test
    @DisplayName("Возвращяет количество метрик")
    public void testGetMetricsSize() {
        List<Metric> mockMetrics = new ArrayList<>();
        mockMetrics.add(new Metric(1, "hotwatr"));
        mockMetrics.add(new Metric(2, "coldWater"));

        when(mockRepository.findAllMetrics()).thenReturn(mockMetrics);

        int result = metricService.getMetricsSize();

        assertEquals(mockMetrics.size(), result);
    }

    @Test
    @DisplayName("Добавляет новую метрику")
    public void testAddNewMetric() {

        metricService.addNewMetric("New Metric");

        verify(mockRepository).save(any(Metric.class));
    }
}