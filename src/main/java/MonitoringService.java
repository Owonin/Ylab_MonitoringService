import auth.AuthContext;
import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.Role;
import domain.model.User;
import domain.repository.MetricRecordRepository;
import domain.repository.MetricRepository;
import domain.repository.UserRepository;
import domain.repository.impl.InMemoryMetricRecordRepository;
import domain.repository.impl.InMemoryMetricRepository;
import domain.repository.impl.InMemoryUserRepository;
import in.AppUI;
import service.MetricRecordService;
import service.MetricService;
import service.UserService;
import service.impl.MetricRecordServiceImpl;
import service.impl.MetricServiceImpl;
import service.impl.UserServiceImpl;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Главный класс приложения MonitoringService. Здесь создаются и настраиваются все компоненты приложения,
 * а также запускается главное меню.
 */
public class MonitoringService {

    public static void main(String[] args) {

        // Создание контекста аутентификации
        AuthContext authContext = new AuthContext();

        // Создание репозиториев пользователей, метрик и записей о показаниях
        UserRepository userRepository = new InMemoryUserRepository();
        MetricRepository metricRepository = new InMemoryMetricRepository();
        MetricRecordRepository metricRecordRepository = new InMemoryMetricRecordRepository();

        // Создание сервисов для работы с пользователями, записями о показаниях и метриками
        UserService userService = new UserServiceImpl(userRepository, authContext);
        MetricRecordService meterRecordService = new MetricRecordServiceImpl(metricRecordRepository, userRepository);
        MetricService metricService = new MetricServiceImpl(metricRepository);

        // Создание пользовательского интерфейса приложения
        AppUI appUI = new AppUI(authContext, userService, meterRecordService, metricService);

        // Инициализация начальных данных
        bootstrap(metricService, userService, metricRecordRepository);

        // Запуск главного меню приложения
        appUI.createMainMenu().run();

    }

    /**
     * Метод для инициализации начальных данных приложения.
     *
     * @param metricService          Сервис для работы с метриками.
     * @param userService            Сервис для работы с пользователями.
     * @param metricRecordRepository Репозиторий записей о показаниях метрик.
     */
    private static void bootstrap(MetricService metricService, UserService userService, MetricRecordRepository metricRecordRepository) {

        // Добавление стандартных метрик
        metricService.addNewMetric("Горячая вода");
        metricService.addNewMetric("Холодная вода");
        metricService.addNewMetric("Отопление");

        List<Metric> metricList = metricService.getAllMetric();

        // Инициализация начальных пользователей и показаний
        try {
            String username1 = "User";
            String username2 = "User2";
            String username3 = "User3";

            String password = "psw";

            userService.registrateUser(username1, password, Set.of(Role.USER));
            userService.registrateUser(username2, password, Set.of(Role.USER));
            userService.registrateUser(username3, password, Set.of(Role.USER));

            List<Metric> metrics = metricService.getAllMetric();
            List<User> users = userService.getAllUsers();

            for (User user : users) {
                for (int i = 1; i < 4; i++) {
                    Map<Metric, Integer> metricsValues = new HashMap<>();
                    for (Metric metric : metrics) {
                        metricsValues.put(metric, 100);
                    }
                    MetricRecord metricRecord = new MetricRecord(metricsValues, LocalDate.of(2023, 9 + i, 11), user);
                    metricRecordRepository.saveMetricForUser(user, metricRecord);
                }
            }

            userService.registrateUser("Admin", password, Set.of(Role.ADMIN));

        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
        }

    }
}
