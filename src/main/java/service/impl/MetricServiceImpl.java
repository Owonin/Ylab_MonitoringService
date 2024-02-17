package service.impl;

import aop.annotations.Loggable;
import domain.model.Metric;
import domain.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.MetricService;

import java.util.List;

/**
 * Класс, представляющий реализацию сервиса работы с метриками.
 */
@Service
@Loggable
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

    private final MetricRepository repository;

    /**
     * Возвращает список всех существующих метрик.
     *
     * @return Список всех метрик.
     */
    @Override
    public List<Metric> getAllMetric() {
        return repository.findAllMetrics();
    }

    /**
     * Возвращает количество имеющихся метрик.
     *
     * @return Количество метрик.
     */
    @Override
    public int getMetricsSize() {
        return repository.findAllMetrics().size();
    }

    /**
     * Добавляет новую метрику с указанным именем.
     *
     * @param name Имя новой метрики.
     */
    @Override
    public void addNewMetric(String name) {
        Metric metric = new Metric(1,name);
        repository.save(metric);
    }

}
