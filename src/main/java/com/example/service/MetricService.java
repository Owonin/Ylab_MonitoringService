package com.example.service;

import com.example.domain.model.Metric;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Интерфейс, определяющий операции для работы с метриками.
 * Предоставляет методы для добавления новых метрик, получения количества метрик и получения списка всех метрик.
 */
@Service
public interface MetricService {

    /**
     * Добавляет новую метрику с указанным именем.
     *
     * @param name Имя новой метрики.
     */
    void addNewMetric(String name);

    /**
     * Возвращает количество имеющихся метрик.
     *
     * @return Количество метрик.
     */
    int getMetricsSize();

    /**
     * Возвращает список всех существующих метрик.
     *
     * @return Список всех метрик.
     */
    List<Metric> getAllMetric();
}