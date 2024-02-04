package service.impl;

import domain.exception.UserNotFoundException;
import domain.model.User;
import domain.repository.AuditRepository;
import domain.repository.UserRepository;
import service.AuditService;

public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    public AuditServiceImpl(AuditRepository auditRepository, UserRepository userRepository) {
        this.auditRepository = auditRepository;
        this.userRepository = userRepository;
    }

    /**
     * Сохраение данных совершенных действий анонимного пользователя
     *
     * @param event    Действие пользователя
     */
    @Override
    public void log(String event) {
        auditRepository.save(event, null);
    }

    /**
     * Сохраение данных совершенных действий пользователя
     *
     * @param event Действие пользователя
     * @param username  Пользователь совершивший действие
     */
    @Override
    public void log(String event, String username) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Пользователь "+username +" не найден"));
        auditRepository.save(event,user);
    }
}