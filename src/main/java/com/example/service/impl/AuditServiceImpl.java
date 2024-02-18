package com.example.service.impl;

import com.example.domain.exception.UserNotFoundException;
import com.example.domain.model.AuditEvent;
import com.example.domain.model.User;
import com.example.domain.repository.AuditRepository;
import com.example.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.service.AuditService;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    /**
     * Сохраение данных совершенных действий анонимного пользователя
     *
     * @param event Действие пользователя
     */
    @Override
    public void log(String event) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setEvent(event);
        auditEvent.setUser(null);
        auditRepository.save(auditEvent);
    }

    /**
     * Сохраение данных совершенных действий пользователя
     *
     * @param event    Действие пользователя
     * @param username Пользователь совершивший действие
     */
    @Override
    public void log(String event, String username) throws UserNotFoundException {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setEvent(event);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь " + username + " не найден"));

        auditEvent.setUser(user);
        auditRepository.save(auditEvent);
    }
}
