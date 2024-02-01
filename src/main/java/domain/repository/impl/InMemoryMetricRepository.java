package domain.repository.impl;

import domain.repository.MetricRepository;
import domain.model.Metric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс, реализующий хранений метрик в оперативной памяти.
 */
public class InMemoryMetricRepository implements MetricRepository {

    private final Set<Metric> metrics = new HashSet<>();

    /**
     * Добавляет новую метрику в репозиторий.
     *
     * @param metric Метрика, которую необходимо добавить.
     * @return true, если метрика успешно добавлена, иначе false.
     */
    @Override
    public boolean addMetric(Metric metric) {
        metrics.add(metric);
        return true;
    }

    /**
     * Возвращает список всех метрик из репозитория.
     *
     * @return Список всех метрик из репозитория.
     */
    @Override
    public List<Metric> allMetrics() {
        return new ArrayList<>(metrics);
    }
}
