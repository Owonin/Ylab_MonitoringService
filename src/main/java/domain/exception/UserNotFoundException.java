package domain.exception;

/**
 * Исключение, выбрасываемое при отсутствии пользователя.
 */
public class UserNotFoundException extends NotFoundException {

    /**
     * Конструктор с сообщением об ошибке о отсутствии пользователя с указанным именем.
     *
     * @param username Имя пользователя, которого не удалось найти.
     */
    public UserNotFoundException(String username) {
        super(String.format("Пользователь с ником %s не был найден", username));
    }
}