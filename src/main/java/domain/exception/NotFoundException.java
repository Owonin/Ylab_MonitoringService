package domain.exception;

/**
 * Исключение, выбрасываемое при отсутствии данных.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке о отсутствии данных.
     *
     * @param message Сообщение об ошибке
     */
    public NotFoundException(String message) {
        super(message);
    }
}
