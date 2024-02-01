package domain.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuditLogTest {

    @BeforeEach
    public void setUp() {
        AuditLog.getAuditRecords().clear(); // Очистка журнала аудита перед каждым тестом
    }

    @Test
    public void testLogAddsActionToAuditLog() {
        AuditLog.log("1");

        List<String> auditRecords = AuditLog.getAuditRecords();
        assertEquals(1, auditRecords.size());
    }

    @Test
    public void testLogAddsActionWithTimestampToAuditLog() {
        LocalDateTime currentTime = LocalDateTime.now();

        AuditLog.log("2");

        List<String> auditRecords = AuditLog.getAuditRecords();
        assertEquals(1, auditRecords.size());

        String[] parts = auditRecords.get(0).split(" : ");
        assertEquals(2, parts.length);
        assertTrue(parts[0].startsWith(currentTime.toLocalDate().toString()));
        assertEquals("2", parts[1]);
    }

    @Test
    public void testGetAuditRecords_ReturnsAllAuditRecords() {
        AuditLog.log("1");
        AuditLog.log("2");
        AuditLog.log("3");

        List<String> auditRecords = AuditLog.getAuditRecords();

        assertEquals(3, auditRecords.size());
        assertTrue(auditRecords.stream().anyMatch(record -> record.contains("1")));
        assertTrue(auditRecords.stream().anyMatch(record -> record.contains("2")));
        assertTrue(auditRecords.stream().anyMatch(record -> record.contains("3")));
    }
}