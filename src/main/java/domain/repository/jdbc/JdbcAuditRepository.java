package domain.repository.jdbc;

import domain.model.AuditEvent;
import domain.repository.AuditRepository;
import util.DBConnectionProvider;

import java.sql.*;
import java.time.LocalDate;

/**
 *
 */
public class JdbcAuditRepository implements AuditRepository {

    private final String insertUserSql = "INSERT INTO private_schema.events (event, user_id, event_time) VALUES (?,?,?)";

    private final DBConnectionProvider connectionProvider;

    /**
     * Конструктор репозитория
     *
     * @param connectionProvider Данные о соединении
     */
    public JdbcAuditRepository(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Сохранить данные аудита
     *
     * @param auditEvent Событие
     */
    @Override
    public AuditEvent save(AuditEvent auditEvent) {
        try (Connection connection = connectionProvider.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, auditEvent.getEvent());
                if (auditEvent.getUser() == null) {
                    preparedStatement.setNull(2, Types.INTEGER);
                } else {
                    preparedStatement.setInt(2, auditEvent.getUser().getUserId());
                }
                preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            auditEvent.setId(generatedKeys.getInt(1));
                            connection.commit();
                            return auditEvent;
                        }
                    }
                    connection.commit();
                }
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Ошибка во время выполнения SQL запроса " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка соединения с БД " + e.getMessage());
        }
        return null;
    }
}
