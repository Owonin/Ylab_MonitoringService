package util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс инициализации миграции БД
 */
public class MigrationExecutor {
    /**
     * Начало выполнения скриптов миграции
     *
     * @param connectionProvider Данные о соединении с БД
     * @param changeLogFile      Название файла скрипта миграции
     */
    public static void execute(DBConnectionProvider connectionProvider, String changeLogFile){
        try (Connection connection = connectionProvider.getConnection()){
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.err.println("SQL Exception in migration " + e.getMessage());
        }
    }
}
