package domain.repository.jdbc;

import domain.model.AuditEvent;
import domain.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import util.ConfigReader;
import util.DBConnectionProvider;
import util.MigrationExecutor;

import static org.junit.jupiter.api.Assertions.*;

class JdbcAuditRepositoryTest {

    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.1-alpine"
    );

    private static DBConnectionProvider connectionProvider;

    private final JdbcAuditRepository auditRepository = new JdbcAuditRepository(connectionProvider);

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        String default_schema = ConfigReader.getInstance().getProperty("DEFAULT_SCHEMA");
        MigrationExecutor.execute(connectionProvider, "db.changelog/test_changelog.xml", default_schema);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void testSaveAuditToUser() {
        User user = new User();
        user.setUserId(1);

        AuditEvent expectedAuditEvent = new AuditEvent();
        expectedAuditEvent.setEvent("Event");
        expectedAuditEvent.setUser(user);

        AuditEvent auditEvent = auditRepository.save(expectedAuditEvent);

        assertNotNull(auditEvent);
        assertEquals(1, auditEvent.getId());
    }
}