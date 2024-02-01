package domain.repository.impl;

import domain.model.MetricRecord;
import domain.model.User;
import domain.repository.MetricRecordRepository;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Класс, реализующий хранение записей о метриках в оперативной памяти.
 */
public class InMemoryMetricRecordRepository implements MetricRecordRepository {

    private final Map<User, List<MetricRecord>> metricMap = new HashMap<>();


    /**
     * Возвращает последнюю запись метрик для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашивается последняя запись метрик.
     * @return Опциональный объект, содержащий последнюю запись метрик пользователя.
     */
    @Override
    public Optional<MetricRecord> getLastMetricForUser(User user) {
        List<MetricRecord> userMetrics = metricMap.get(user);
        if (userMetrics != null && !userMetrics.isEmpty()) {
            return Optional.of(userMetrics.get(userMetrics.size() - 1));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Возвращает список записей метрик для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашиваются записи метрик.
     * @return Список записей метрик пользователя.
     */
    @Override
    public List<MetricRecord> getUserMetrics(User user) {
        return metricMap.getOrDefault(user, List.of());
    }

    /**
     * Сохраняет запись метрик для указанного пользователя.
     *
     * @param user   Пользователь, для которого сохраняется запись метрик.
     * @param metric Запись метрик, которую необходимо сохранить.
     * @return true, если запись метрик успешно сохранена, иначе false.
     */
    @Override
    public boolean saveMetricForUser(User user, MetricRecord metric) {
        if (metricMap.get(user) == null) {
            metricMap.put(user, new ArrayList<>(List.of(metric)));
            return true;
        }
        metricMap.get(user).add(metric);
        return true;
    }

    /**
     * Возвращает список всех записей метрик из репозитория.
     *
     * @return Список всех записей метрик из репозитория.
     */
    @Override
    public List<MetricRecord> getAllMetrics() {
        return metricMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

}
