package com.example.service.impl;

import com.example.aop.annotations.Loggable;
import com.example.domain.exception.MetricRecordNotFoundException;
import com.example.domain.exception.NotFoundException;
import com.example.domain.exception.UserNotFoundException;
import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.domain.model.User;
import com.example.domain.repository.MetricRecordRepository;
import com.example.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.service.MetricRecordService;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс, представляющий реализацию сервиса работы с записями о метриках.
 */
@Service
@Loggable
@RequiredArgsConstructor
public class MetricRecordServiceImpl implements MetricRecordService {

    private final MetricRecordRepository metricRecordRepository;
    private final UserRepository userRepository;

    /**
     * Возвращает список записей метрик для указанного пользователя.
     *
     * @param username Имя пользователя, для которого запрашиваются записи метрик.
     * @return Список записей метрик пользователя.
     * @throws NotFoundException Если пользователь с указанным именем не найден.
     */
    @Override
    public List<MetricRecord> getUserMetrics(String username) throws NotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return metricRecordRepository.findUserMetrics(user);
    }

    /**
     * Возвращает список всех записей метрик всех пользователей.
     *
     * @return Список всех записей метрик всех пользователей.
     */
    @Override
    public List<MetricRecord> getAllUsersMetrics() {
        return metricRecordRepository.getAllMetrics();
    }

    @Override
    public MetricRecord getLastMetricRecord(String username) throws NotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return metricRecordRepository.findLastMetricForUser(user)
                .orElseThrow(() -> new MetricRecordNotFoundException(username));
    }

    /**
     * Добавляет новую запись метрик для указанного пользователя за текущий месяц.
     *
     * @param username Имя пользователя, для которого добавляется запись метрик.
     * @param metrics  Карта, содержащая метрики и их значения для добавления в запись.
     * @return true, если запись метрик успешно добавлена, иначе false.
     * @throws NotFoundException Если пользователь с указанным именем не найден.
     */
    @Override
    public boolean addNewMonthlyMetric(String username, Map<Metric, Integer> metrics) throws NotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<MetricRecord> lastMetrics = metricRecordRepository.findUserMetrics(user);
        int lastMetricsSize = lastMetrics.size();

        MetricRecord newRecord = new MetricRecord(1, new HashMap<>(metrics), LocalDate.now(), user);

        Month currentMonth = LocalDate.now().getMonth();

        if (lastMetrics.isEmpty()) {
            metricRecordRepository.save(user, newRecord);
            return true;
        } else {
            MetricRecord lastMetric = lastMetrics.get(lastMetricsSize - 1);

            if (lastMetric.getMetricDate().getMonth() != currentMonth) {
                metricRecordRepository.save(user, newRecord);
                return true;
            }

            return false;
        }
    }

    /**
     * Возвращает запись метрик для указанного пользователя за указанный месяц и год.
     *
     * @param month    Номер месяца.
     * @param year     Год.
     * @param username Имя пользователя, для которого запрашивается запись метрик.
     * @return Запись метрик пользователя за указанный месяц и год.
     * @throws NotFoundException Если запись метрик за указанный месяц и год не найдена или пользователь с указанным именем не найден.
     */
    @Override
    public MetricRecord getMetricRecordByMonth(String username, int month, int year) throws NotFoundException {
        LocalDate requiredDate = LocalDate.of(year, month, 1);
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<MetricRecord> metrics = metricRecordRepository.findUserMetrics(user);

        return metrics.stream()
                .filter(date -> (date.getMetricDate().getMonthValue() == requiredDate.getMonthValue()) &&
                        (date.getMetricDate().getYear() == requiredDate.getYear()))
                .findFirst()
                .orElseThrow(() -> new MetricRecordNotFoundException(username));
    }
}
