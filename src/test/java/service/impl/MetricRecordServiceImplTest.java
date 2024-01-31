package service.impl;

import domain.exception.NotFoundException;
import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.User;
import domain.repository.MetricRecordRepository;
import domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.MetricRecordService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MetricRecordServiceImplTest {

    @Mock
    private MetricRecordRepository metricRecordRepository;

    @Mock
    private UserRepository userRepository;

    private MetricRecordService metricRecordService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        metricRecordService = new MetricRecordServiceImpl(metricRecordRepository, userRepository);
    }

    @Test
    public void testGetUserMetrics() throws NotFoundException {
        User user = new User("id", "username");

        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric("id", "electricity"), 100);
        metrics.put(new Metric("id2", "water"), 100);
        MetricRecord metricRecord = new MetricRecord(metrics, LocalDate.now(), user);

        List<MetricRecord> expectedMetrics = Collections.singletonList(metricRecord);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.getUserMetrics(user)).thenReturn(expectedMetrics);

        List<MetricRecord> actualMetrics = metricRecordService.getUserMetrics("username");
        assertEquals(expectedMetrics, actualMetrics);
    }

    @Test
    public void testGetAllUsersMetrics() {
        List<MetricRecord> expectedMetrics = Collections.emptyList();
        when(metricRecordRepository.getAllMetrics()).thenReturn(expectedMetrics);

        List<MetricRecord> actualMetrics = metricRecordService.getAllUsersMetrics();
        assertEquals(expectedMetrics, actualMetrics);
    }

    @Test
    public void testGetLastMetricRecord() throws NotFoundException {
        User user = new User("id", "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric("id", "hotwater"), 100);
        metrics.put(new Metric("id2", "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(metrics, LocalDate.now(), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.getLastMetricForUser(user)).thenReturn(Optional.of(metricRecord));

        MetricRecord actualMetricRecord = metricRecordService.getLastMetricRecord("username");
        assertEquals(metricRecord, actualMetricRecord);
    }

    @Test
    public void testAddNewMonthlyMetric() throws NotFoundException {
        User user = new User("id", "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric("id", "electricity"), 100);
        metrics.put(new Metric("id2", "water"), 100);
        MetricRecord metricRecord = new MetricRecord(metrics, LocalDate.now().minusMonths(1), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.getUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));
        when(metricRecordRepository.saveMetricForUser(any(User.class), any(MetricRecord.class))).thenReturn(true);

        boolean added = metricRecordService.addNewMonthlyMetric("username", metrics);
        assertTrue(added);
    }

    @Test
    public void testAddNewMonthlyMetricWithSameMonth() throws NotFoundException {
        User user = new User("id", "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric("id", "electricity"), 100);
        metrics.put(new Metric("id2", "water"), 100);
        MetricRecord metricRecord = new MetricRecord(metrics, LocalDate.now(), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.getUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));
        when(metricRecordRepository.saveMetricForUser(any(User.class), any(MetricRecord.class))).thenReturn(true);

        boolean added = metricRecordService.addNewMonthlyMetric("username", metrics);
        assertFalse(added);
    }

    @Test
    public void testGetMetricRecordByMonth() throws NotFoundException {
        User user = new User("id", "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric("id", "hotwater"), 100);
        metrics.put(new Metric("id2", "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(metrics, LocalDate.of(1999, 12, 11), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.getUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));

        MetricRecord actualMetricRecord = metricRecordService.getMetricRecordByMonth(12, 1999, "username");
        assertEquals(metricRecord, actualMetricRecord);
    }

    @Test
    public void testGetMetricRecordByMonthNotFound() {
        User user = new User("id", "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric("id", "hotwater"), 100);
        metrics.put(new Metric("id2", "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(metrics, LocalDate.of(2000, 11, 11), user);

        when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(user));
        when(metricRecordRepository.getUserMetrics(user)).thenReturn(Collections.singletonList(metricRecord));


        assertThrows(NotFoundException.class, () -> {
            metricRecordService.getMetricRecordByMonth(12, 1999, "username");
        });
    }
}