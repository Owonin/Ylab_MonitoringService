package service.impl;

import domain.model.Metric;
import domain.repository.MetricRepository;
import service.MetricService;

import java.util.List;
import java.util.UUID;

/**
 * Класс, представляющий реализацию сервиса работы с метриками.
 */
public class MetricServiceImpl implements MetricService {

    private final MetricRepository repository;

    /**
     * Конструктор класса
     *
     * @param repository  Репозиторий метрик.
     */
    public MetricServiceImpl(MetricRepository repository) {
        this.repository = repository;
    }

    /**
     * Возвращает список всех существующих метрик.
     *
     * @return Список всех метрик.
     */
    @Override
    public List<Metric> getAllMetric() {
        return repository.allMetrics();
    }

    /**
     * Возвращает количество имеющихся метрик.
     *
     * @return Количество метрик.
     */
    @Override
    public int getMetricsSize() {
        return repository.allMetrics().size();
    }

    /**
     * Добавляет новую метрику с указанным именем.
     *
     * @param name Имя новой метрики.
     */
    @Override
    public void addNewMetric(String name) {
        Metric metric = new Metric(UUID.randomUUID().toString(), name);
        repository.addMetric(metric);
    }

}
