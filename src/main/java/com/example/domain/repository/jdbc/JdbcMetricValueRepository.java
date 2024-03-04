package com.example.domain.repository.jdbc;

import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.domain.repository.MetricValueRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class JdbcMetricValueRepository implements MetricValueRepository {

    public static final String INSERT_INTO_METRIC_VALUES = "INSERT INTO private_schema.metric_values " +
            "(value, metric_record_id, metric_id) VALUES (?, ?, ?)";

    /**
     * Сохранение значений записей метрик в БД
     *
     * @param connection Соединение с БД
     * @param metrics    Запись метрик
     */
    @Override
    public void saveMetricValue(Connection connection, MetricRecord metrics) {
        for (Metric metric : metrics.getMetrics().keySet()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    INSERT_INTO_METRIC_VALUES)) {
                statement.setInt(1, metrics.getMetrics().get(metric));
                statement.setInt(2, metrics.getId());
                statement.setInt(3, metric.getId());

                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
            }
        }

    }
}
