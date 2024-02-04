package domain.repository.jdbc;

import domain.model.AuditEvent;
import domain.model.User;
import domain.repository.AuditRepository;
import util.DBConnectionProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Types;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;

/**
 *
 */
public class JdbcAuditRepository implements AuditRepository {

    private final String INSERT_USER_SQL = "INSERT INTO private_schema.events (event, user_id, event_time) VALUES (?,?,?)";

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
     * @param event Событие
     * @param user  Пользователь
     */
    @Override
    public AuditEvent save(String event, User user) {
        try (Connection connection = connectionProvider.getConnection()) {

            connection.setAutoCommit(false);


            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, event);
                if (user == null) {
                    preparedStatement.setNull(2, Types.INTEGER);
                } else {
                    preparedStatement.setInt(2, user.getUserId());
                }
                preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
                int affectedRows = preparedStatement.executeUpdate();
                AuditEvent auditEvent = new AuditEvent();
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
