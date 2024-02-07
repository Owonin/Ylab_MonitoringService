package domain.repository;

import domain.model.AuditEvent;
import domain.model.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс, определяющий операции для работы с записями аудита.
 */
public interface AuditRepository {
    /**
     * Сохранить данные аудита
     */
    AuditEvent save(AuditEvent auditEvent);

}
