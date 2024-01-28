package domain.exception;

/**
 * Исключение, выбрасываемое при отсутствии записей метрик для пользователя.
 */
public class MetricRecordNotFoundException extends NotFoundException {

    /**
     * Конструктор с сообщением об ошибке о отсутствии записей метрик для пользователя.
     *
     * @param username Имя пользователя, для которого не найдены записи метрик.
     */
    public MetricRecordNotFoundException(String username) {
        super(String.format("Метрик пользователя %s не было найдено", username));
    }
}
