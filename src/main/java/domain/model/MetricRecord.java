package domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Класс, представляющий запись метрик.
 */
@Getter
@Setter
public class MetricRecord {

    /**
     * Конструктор для создания новой метрики с указанным идентификатором и именем.
     *
     * @param metrics     Список метрик и их значений.
     * @param metricDate  Дата создания метрики.
     * @param user        Пользователь, создавший запись.
     */
    public MetricRecord(Map<Metric, Integer> metrics, LocalDate metricDate, User user) {
        this.metrics = metrics;
        this.metricDate = metricDate;
        this.user = user;
    }

    private Map<Metric, Integer> metrics;
    private LocalDate metricDate;
    private User user;

}
