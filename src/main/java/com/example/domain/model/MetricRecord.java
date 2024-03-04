package com.example.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Класс, представляющий запись метрик.
 */
@Getter
@Setter
@EqualsAndHashCode
public class MetricRecord {

    private Integer id;
    private Map<Metric, Integer> metrics;
    private LocalDate metricDate;
    private User user;

    /**
     * Конструктор для создания новой метрики с указанным идентификатором и именем.
     *
     * @param id          Индификатор метрики
     * @param metrics     Список метрик и их значений.
     * @param metricDate  Дата создания метрики.
     * @param user        Пользователь, создавший запись.
     */
    public MetricRecord(int id, Map<Metric, Integer> metrics, LocalDate metricDate, User user) {
        this.id = id;
        this.metrics = metrics;
        this.metricDate = metricDate;
        this.user = user;
    }
}
