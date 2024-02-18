package com.example.service.impl;

import com.example.domain.exception.NotFoundException;
import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.domain.model.User;
import com.example.domain.repository.MetricRecordRepository;
import com.example.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricRecordServiceImplTest {

    @Mock
    private MetricRecordRepository metricRecordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MetricRecordServiceImpl metricRecordService;

    @Test
    @DisplayName("Возвращает метрики пользователя")
    public void testGetUserMetrics() throws NotFoundException {
        User user = new User(1, "username");

        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.now(), user);

        List<MetricRecord> expectedMetrics = Collections.singletonList(metricRecord);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.findUserMetrics(user)).thenReturn(expectedMetrics);

        List<MetricRecord> actualMetrics = metricRecordService.getUserMetrics("username");
        assertEquals(expectedMetrics, actualMetrics);
    }

    @Test
    @DisplayName("Возвращает метрики всех пользователей")
    public void testGetAllUsersMetrics() {
        List<MetricRecord> expectedMetrics = Collections.emptyList();
        when(metricRecordRepository.getAllMetrics()).thenReturn(expectedMetrics);

        List<MetricRecord> actualMetrics = metricRecordService.getAllUsersMetrics();
        assertEquals(expectedMetrics, actualMetrics);
    }

    @Test
    @DisplayName("Возвращает запись метрик пользователя")
    public void testGetLastMetricRecord() throws NotFoundException {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "hotwater"), 100);
        metrics.put(new Metric(2, "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.now(), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.findLastMetricForUser(user)).thenReturn(Optional.of(metricRecord));

        MetricRecord actualMetricRecord = metricRecordService.getLastMetricRecord("username");
        assertEquals(metricRecord, actualMetricRecord);
    }

    @Test
    @DisplayName("Добавление новой ежемесячной метрики")
    public void testAddNewMonthlyMetric() throws NotFoundException {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.now().minusMonths(1), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.findUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));
        when(metricRecordRepository.save(any(User.class), any(MetricRecord.class))).thenReturn(metricRecord);

        boolean added = metricRecordService.addNewMonthlyMetric("username", metrics);
        assertTrue(added);
    }

    @Test
    @DisplayName("Добавление новой ежемесячной метрики в один и тотже месяц возвращяет false")
    public void testAddNewMonthlyMetricWithSameMonth() throws NotFoundException {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.now(), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.findUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));

        boolean added = metricRecordService.addNewMonthlyMetric("username", metrics);
        assertFalse(added);
    }

    @Test
    @DisplayName("Получений новой метрики по месяцу")
    public void testGetMetricRecordByMonth() throws NotFoundException {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "hotwater"), 100);
        metrics.put(new Metric(2, "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.of(1999, 12, 11), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.findUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));

        MetricRecord actualMetricRecord = metricRecordService.getMetricRecordByMonth("username", 12, 1999);
        assertEquals(metricRecord, actualMetricRecord);
    }

    @Test
    @DisplayName("Получений новой метрики по месяцу врзвращяет ошибку поиска")
    public void testGetMetricRecordByMonthNotFound() {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "hotwater"), 100);
        metrics.put(new Metric(2, "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.of(2000, 11, 11), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.findUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));


        assertThrows(NotFoundException.class, () -> metricRecordService.getMetricRecordByMonth("username",12, 1999));
    }
}