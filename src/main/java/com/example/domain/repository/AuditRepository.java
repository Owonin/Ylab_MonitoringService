package com.example.domain.repository;

import com.example.domain.model.AuditEvent;
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
