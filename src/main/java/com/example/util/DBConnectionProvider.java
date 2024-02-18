package com.example.util;

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
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private  String username;
    @Value("${spring.datasource.password}")
    private String password;

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
