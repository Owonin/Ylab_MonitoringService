package initializer;

import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import domain.repository.AuditRepository;
import domain.repository.MetricRecordRepository;
import domain.repository.MetricRepository;
import domain.repository.UserRepository;
import domain.repository.jdbc.JdbcAuditRepository;
import domain.repository.jdbc.JdbcMetricRecordRepository;
import domain.repository.jdbc.JdbcMetricRepository;
import domain.repository.jdbc.JdbcUserRepository;
import service.AuditService;
import service.MetricRecordService;
import service.MetricService;
import service.UserService;
import service.impl.AuditServiceImpl;
import service.impl.MetricRecordServiceImpl;
import service.impl.MetricServiceImpl;
import service.impl.UserServiceImpl;
import util.ConfigReader;
import util.DBConnectionProvider;
import util.MigrationExecutor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {

    /**
     * Инициалиация начальных параметров
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConfigReader configReader = ConfigReader.getInstance();

        String defaultLiquebaseSchema = configReader.getProperty("DEFAULT_SCHEMA");

        //Создание данных о соединении
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));


        // Создание репозиториев пользователей, метрик и записей о показаниях
        UserRepository userRepository = new JdbcUserRepository(connectionProvider);
        MetricRepository metricRepository = new JdbcMetricRepository(connectionProvider);
        MetricRecordRepository metricRecordRepository = new JdbcMetricRecordRepository(connectionProvider);
        AuditRepository auditRepository = new JdbcAuditRepository(connectionProvider);

        // Создание сервисов для работы с пользователями, записями о показаниях и метриками
        UserService userService = new UserServiceImpl(userRepository);
        MetricRecordService meterRecordService = new MetricRecordServiceImpl(metricRecordRepository, userRepository);
        MetricService metricService = new MetricServiceImpl(metricRepository);
        AuditService auditService = new AuditServiceImpl(auditRepository, userRepository);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        MigrationExecutor.execute(connectionProvider, "db.changelog/changelog.xml", defaultLiquebaseSchema);

        AuthContextFactory authContextFactory = new AuthContextFactory();

        ServletContext servletContext = sce.getServletContext();

        servletContext.setAttribute("objectMapper", objectMapper);

        servletContext.setAttribute("authContextFactory", authContextFactory);

        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("meterRecordService", meterRecordService);
        servletContext.setAttribute("metricService", metricService);
        servletContext.setAttribute("auditService", auditService);
    }
}
