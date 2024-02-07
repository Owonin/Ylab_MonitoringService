package util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс инициализации миграции БД
 */
public class MigrationExecutor {

    private static final String CREATE_DEFAULT_LIQUEBASE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS \"%s\"";

    /**
     * Начало выполнения скриптов миграции
     *
     * @param connectionProvider Данные о соединении с БД
     * @param changeLogFile      Название файла скрипта миграции
     */
    public static void execute(DBConnectionProvider connectionProvider, String changeLogFile, String defaultLiquebaseSchemaName) {

        try (Connection connection = connectionProvider.getConnection()) {

            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format(CREATE_DEFAULT_LIQUEBASE_SCHEMA_SQL,defaultLiquebaseSchemaName));

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName(defaultLiquebaseSchemaName);

            Liquibase liquibase =
                    new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.err.println("SQL Exception in migration " + e.getMessage());
        }
    }
}
