package domain.repository;

import domain.model.AuditEvent;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс, определяющий операции для работы с записями аудита.
 */
@Repository
public interface AuditRepository {
    /**
     * Сохранить данные аудита
     */
    AuditEvent save(AuditEvent auditEvent);

}
