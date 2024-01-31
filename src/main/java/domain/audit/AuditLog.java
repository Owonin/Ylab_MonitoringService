package domain.audit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий журнал аудита действий пользователей.
 */
public class AuditLog {

    private static final List<String> auditRecords = new ArrayList<>();

    /**
     * Записывает действие пользователя в журнал аудита с указанием временной метки.
     *
     * @param action Действие пользователя для записи в журнал аудита.
     */
    public static void log(String action) {
        LocalDateTime timestamp = LocalDateTime.now();
        String record = timestamp + " : " + action;
        auditRecords.add(record);
    }

    /**
     * Возвращает список всех записей в журнале аудита.
     *
     * @return Список всех записей в журнале аудита.
     */
    public static List<String> getAuditRecords() {
        return auditRecords;
    }
}