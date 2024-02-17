package domain.repository;

import domain.model.Metric;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий операции для работы с метриками в репозитории.
 */
@Repository
public interface MetricRepository {

    /**
     * Добавляет новую метрику в репозиторий.
     *
     * @param metric Метрика, которую необходимо добавить.
     * @return Сохраненная метрика, иначе null
     */
    Metric save(Metric metric);

    /**
     * Возвращает список всех метрик из репозитория.
     *
     * @return Список всех метрик из репозитория.
     */
    List<Metric> findAllMetrics();


    /**
     * Возвращает метрику по названию метрики
     *
     * @return Опциональный объект, содержащий метрику
     */
    Optional<Metric> findMetricByName(String name);
}