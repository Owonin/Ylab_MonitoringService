package util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Класс хранящий данные о подключении к БД
 */
public class DBConnectionProvider {
    private final String url;
    private final String username;
    private final String password;

    /**
     * Конструктор класса
     *
     * @param url       URL строка для подключения к БД
     * @param username  Имя пользователя БД
     * @param password  Пароль пользователя БД
     */
    public DBConnectionProvider(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

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
