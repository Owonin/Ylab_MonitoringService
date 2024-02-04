package service;

import domain.exception.UserNotFoundException;
import domain.model.User;

public interface AuditService {

    /**
     * Сохраение данных совершенных действий пользователя
     *
     * @param event      Действие пользователя
     * @param username   Пользователь совершивший действие
     */
    void log(String event, String username) throws UserNotFoundException;

    /**
     * Сохраение данных совершенных действий анонимного пользователя
     *
     * @param event    Действие пользователя
     */
    void log(String event);
}
