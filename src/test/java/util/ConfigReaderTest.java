package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigReaderTest {

    @Test
    @DisplayName("Получение экземпляра ConfigReader не вызывает ошибку")
    void testConfigReaderGetInstanceDoesNotThrowError() {
        assertDoesNotThrow(() -> {
            ConfigReader.getInstance();
        });
    }

    @Test
    @DisplayName("Получение данных конфигурации")
    void testConfigReaderGetPropertyReturnProperty() {
        String expectedOutput = "testValue";

        String instance = ConfigReader.getInstance().getProperty("ConfigReaderTestProperty");

        assertEquals(instance, expectedOutput);
    }

    @Test
    @DisplayName("Вызов ошибки при получении несуществующей конфигурации")
    void testConfigReaderGetPropertyThrowRuntimeErrorWithNonExistingProperty() {
        assertThrows(RuntimeException.class, () -> ConfigReader.getInstance().getProperty("This property is not existing"));
    }
}