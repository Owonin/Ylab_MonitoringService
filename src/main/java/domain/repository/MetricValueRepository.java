package domain.repository;

import domain.model.Metric;
import domain.model.MetricRecord;

import java.sql.Connection;
import java.util.Map;

public interface MetricValueRepository {

    /**
     * Сохранение значений записей метрик в БД
     *
     * @param connection Соединение с БД
     * @param metrics    Запись метрик
     */
    void saveMetricValue(Connection connection, MetricRecord metrics);
}
