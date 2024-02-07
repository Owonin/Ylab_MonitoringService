package service.impl;

import domain.exception.UserNotFoundException;
import domain.model.AuditEvent;
import domain.model.User;
import domain.repository.AuditRepository;
import domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    @DisplayName("Сохранение пользователя как анонимного пользователя")
    void testAnonymousUserSaveToAuditRepository() {
        String event = "Some event";
        auditService.log(event);
        verify(auditRepository, times(1)).save(any(AuditEvent.class));
    }

    @Test
    @DisplayName("Сохранение пользователя как пользователя")
    void logExistingUserSaveToAuditRepository() throws UserNotFoundException {
        String event = "Some event";
        String username = "existingUser";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        auditService.log(event, username);

        verify(auditRepository, times(1)).save(any(AuditEvent.class));
    }

    @Test
    @DisplayName("Сохранение несуществующего пользователя вызывает ошибку")
    void logNonExistingUserThrowUserNotFoundException() {
        String event = "Some event";
        String username = "nonExistingUser";

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> auditService.log(event, username));

        verify(auditRepository, never()).save(any(AuditEvent.class));
    }
}