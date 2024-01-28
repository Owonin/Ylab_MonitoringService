package service;

import domain.exception.NotFoundException;
import domain.model.Metric;
import domain.model.MetricRecord;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс, представляющий сервис работы с записями метрик.
 * Этот интерфейс определяет операции для добавления, получения и просмотра записей метрик.
 */
public interface MetricRecordService {

    /**
     * Добавляет новую запись метрик для указанного пользователя за текущий месяц.
     *
     * @param username Имя пользователя, для которого добавляется запись метрик.
     * @param metrics  Карта, содержащая метрики и их значения для добавления в запись.
     * @return true, если запись метрик успешно добавлена, иначе false.
     * @throws NotFoundException Если пользователь с указанным именем не найден.
     */
    boolean addNewMonthlyMetric(String username, Map<Metric, Integer> metrics) throws NotFoundException;

    /**
     * Возвращает список записей метрик для указанного пользователя.
     *
     * @param username Имя пользователя, для которого запрашиваются записи метрик.
     * @return Список записей метрик пользователя.
     * @throws NotFoundException Если пользователь с указанным именем не найден.
     */
    List<MetricRecord> getUserMetrics(String username) throws NotFoundException;

    /**
     * Возвращает список всех записей метрик всех пользователей.
     *
     * @return Список всех записей метрик всех пользователей.
     */
    List<MetricRecord> getAllUsersMetrics();

    /**
     * Возвращает последнюю запись метрик для указанного пользователя.
     *
     * @param username Имя пользователя, для которого запрашивается последняя запись метрик.
     * @return Последняя запись метрик пользователя.
     * @throws NotFoundException Если пользователь с указанным именем не найден.
     */
    MetricRecord getLastMetricRecord(String username) throws NotFoundException;

    /**
     * Возвращает запись метрик для указанного пользователя за указанный месяц и год.
     *
     * @param month    Номер месяца.
     * @param year     Год.
     * @param username Имя пользователя, для которого запрашивается запись метрик.
     * @return Запись метрик пользователя за указанный месяц и год.
     * @throws NotFoundException Если запись метрик за указанный месяц и год не найдена или пользователь с указанным именем не найден.
     */
    MetricRecord getMetricRecordByMonth(int month, int year, String username) throws NotFoundException;
}