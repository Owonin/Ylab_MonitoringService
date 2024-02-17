package util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Класс хранящий данные о подключении к БД
 */
@Component
@RequiredArgsConstructor
public class DBConnectionProvider {
    @Value("url")
    private final String url;
    @Value("username")
    private final String username;
    @Value("password")
    private final String password;

    /**
     * Получение соединения с БД
     *
     * @return Объект интерфейса Connection
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка во время соединения с БД "+e.getMessage());
        }
    }
}
