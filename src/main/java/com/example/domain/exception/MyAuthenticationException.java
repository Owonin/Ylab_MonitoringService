package com.example.domain.exception;

/**
 * Исключение, выбрасываемое при ошибке аунтификации.
 */
public class MyAuthenticationException extends RuntimeException{
    /**
     * Конструктор с сообщением об ошибке аунтификации.
     *
     * @param message Сообщение об ошибке
     */
    public MyAuthenticationException(String message) {
        super(message);
    }
}
