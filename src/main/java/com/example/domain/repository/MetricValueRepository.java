package com.example.domain.repository;

import com.example.domain.model.MetricRecord;
import org.springframework.stereotype.Repository;

import java.sql.Connection;

@Repository
public interface MetricValueRepository {

    /**
     * Сохранение значений записей метрик в БД
     *
     * @param connection Соединение с БД
     * @param metrics    Запись метрик
     */
    void saveMetricValue(Connection connection, MetricRecord metrics);
}
