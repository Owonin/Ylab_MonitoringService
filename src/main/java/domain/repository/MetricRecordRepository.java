package domain.repository;

import domain.model.MetricRecord;
import domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий операции для работы с записями метрик.
 */
public interface MetricRecordRepository {

    /**
     * Возвращает последнюю запись метрик для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашивается последняя запись метрик.
     * @return Опциональный объект, содержащий последнюю запись метрик пользователя.
     */
    Optional<MetricRecord> getLastMetricForUser(User user);

    /**
     * Возвращает список записей метрик для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашиваются записи метрик.
     * @return Список записей метрик пользователя.
     */
    List<MetricRecord> getUserMetrics(User user);

    /**
     * Сохраняет запись метрик для указанного пользователя.
     *
     * @param user   Пользователь, для которого сохраняется запись метрик.
     * @param metric Запись метрик, которую необходимо сохранить.
     * @return true, если запись метрик успешно сохранена, иначе false.
     */
    boolean saveMetricForUser(User user, MetricRecord metric);

    /**
     * Возвращает список всех записей метрик из репозитория.
     *
     * @return Список всех записей метрик из репозитория.
     */
    List<MetricRecord> getAllMetrics();
}