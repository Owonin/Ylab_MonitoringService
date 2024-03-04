package com.example.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс инициализации миграции БД
 */

@Component
public class MigrationExecutor {

    @Value("${spring.datasource.defaultSchema}")
    private String liquidiseSchemaName;

    @Value("${spring.datasource.changelogFile}")
    private String changeLogFile;
    private final String CREATE_DEFAULT_LIQUEBASE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS \"%s\"";

    /**
     * Начало выполнения скриптов миграции
     *
     * @param connectionProvider Данные о соединении с БД
     */
    public void execute(DBConnectionProvider connectionProvider) {

        try (Connection connection = connectionProvider.getConnection()) {

            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format(CREATE_DEFAULT_LIQUEBASE_SCHEMA_SQL, liquidiseSchemaName));

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName(liquidiseSchemaName);

            Liquibase liquibase =
                    new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.err.println("SQL Exception in migration " + e.getMessage());
        }
    }
}
