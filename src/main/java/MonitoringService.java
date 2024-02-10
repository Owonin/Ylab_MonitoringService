import auth.AuthContext;
import domain.repository.AuditRepository;
import domain.repository.MetricRecordRepository;
import domain.repository.MetricRepository;
import domain.repository.UserRepository;
import domain.repository.jdbc.JdbcAuditRepository;
import domain.repository.jdbc.JdbcMetricRecordRepository;
import domain.repository.jdbc.JdbcMetricRepository;
import domain.repository.jdbc.JdbcUserRepository;
import in.cli.AppUI;
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

/**
 * Главный класс приложения MonitoringService. Здесь создаются и настраиваются все компоненты приложения,
 * а также запускается главное меню.
 */
public class MonitoringService {
    public static void main(String[] args) {

        //Получение объекта для чтения конфиг-файла
        ConfigReader configReader = ConfigReader.getInstance();

        String defaultLiquebaseSchema = configReader.getProperty("DEFAULT_SCHEMA");

        //Создание данных о соединении
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));

        // Создание контекста аутентификации
        AuthContext authContext = new AuthContext();

        // Создание репозиториев пользователей, метрик и записей о показаниях
        UserRepository userRepository = new JdbcUserRepository(connectionProvider);
        MetricRepository metricRepository = new JdbcMetricRepository(connectionProvider);
        MetricRecordRepository metricRecordRepository = new JdbcMetricRecordRepository(connectionProvider);
        AuditRepository auditRepository = new JdbcAuditRepository(connectionProvider);

        // Создание сервисов для работы с пользователями, записями о показаниях и метриками
        UserService userService = new UserServiceImpl(userRepository, authContext);
        MetricRecordService meterRecordService = new MetricRecordServiceImpl(metricRecordRepository, userRepository);
        MetricService metricService = new MetricServiceImpl(metricRepository);
        AuditService auditService = new AuditServiceImpl(auditRepository, userRepository);

        // Создание пользовательского интерфейса приложения
        AppUI appUI = new AppUI(authContext, userService, meterRecordService, metricService, auditService);

        // Инициализация начальных данных
        jdbcBootstrap(connectionProvider, defaultLiquebaseSchema);

        // Запуск главного меню приложения
        appUI.createMainMenu().run();

    }

    /**
     * Инициализация данных в БД
     *
     * @param connectionProvider данные о подключении к БД.
     */
    private static void jdbcBootstrap(DBConnectionProvider connectionProvider, String defaultLiquebaseSchema) {
        MigrationExecutor.execute(connectionProvider, "db.changelog/changelog.xml", defaultLiquebaseSchema);
    }
}
