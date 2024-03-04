package com.example.domain.repository.jdbc;

import com.example.domain.model.AuditEvent;
import com.example.domain.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.example.util.DBConnectionProvider;

import java.sql.*;
import java.time.LocalDate;

/**
 *
 */
@Repository
@RequiredArgsConstructor
public class JdbcAuditRepository implements AuditRepository {

    private final String insertUserSql = "INSERT INTO private_schema.events (event, user_id, event_time) VALUES (?,?,?)";

    private final DBConnectionProvider connectionProvider;

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
