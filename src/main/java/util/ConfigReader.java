package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Класс реализующий чтение конфиг-файла
 */
public class ConfigReader {
    private final Properties properties;
    private static ConfigReader configReader;

    /**
     * Приватный конструктор класса
     */
    private ConfigReader() {
        BufferedReader reader;
        URL resourceUrl = getClass().getResource("/configuration.properties");

        try {
            reader = new BufferedReader(new FileReader(resourceUrl.getPath()));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("Configuration file has not been found");
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Configuration.properties not found at ");
        }
    }

    /**
     * Получение экземпляра объекта класса
     *
     * @return Эеземпляр обекта класса
     */
    public static ConfigReader getInstance() {
        if (configReader == null) {
            configReader = new ConfigReader();
        }
        return configReader;
    }

    /**
     * Чтение параметров конфиг-файла
     *
     * @param property  Параметр конфиг-файла
     * @return          Значение параметра конфиг-файла
     */
    public String getProperty(String property) {
        String baseUrl = properties.getProperty(property);
        if (baseUrl != null) return baseUrl;
        else throw new RuntimeException(String.format("Property %s has not been found", property));
    }
}
