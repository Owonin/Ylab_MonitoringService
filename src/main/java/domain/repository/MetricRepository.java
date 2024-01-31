package domain.repository;

import domain.model.Metric;

import java.util.List;

/**
 * Интерфейс, определяющий операции для работы с метриками в репозитории.
 */
public interface MetricRepository {

    /**
     * Добавляет новую метрику в репозиторий.
     *
     * @param metric Метрика, которую необходимо добавить.
     * @return true, если метрика успешно добавлена, иначе false.
     */
    boolean addMetric(Metric metric);

    /**
     * Возвращает список всех метрик из репозитория.
     *
     * @return Список всех метрик из репозитория.
     */
    List<Metric> allMetrics();
}