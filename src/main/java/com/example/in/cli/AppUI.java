package com.example.in.cli;

import com.example.auth.AuthContext;
import com.example.domain.exception.MyAuthenticationException;
import com.example.domain.exception.NotFoundException;
import com.example.domain.exception.UserNotFoundException;
import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.domain.model.Role;
import com.example.service.AuditService;
import com.example.service.MetricRecordService;
import com.example.service.MetricService;
import com.example.service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Класс, представляющий пользовательский интерфейс приложения.
 * Этот класс управляет взаимодействием пользователя с приложением через консольный интерфейс.
 */
public class AppUI {

    /**
     * Формат вывода даты.
     */
    private final String DATE_FORMAT_PATTERN = "dd LLLL yyyy";

    /**
     * Контекст аутентификации.
     */
    private final AuthContext context;
    /**
     * Сервис пользователей.
     */
    private final UserService userService;
    /**
     * Сервис записей метрик.
     */
    private final MetricRecordService metricRecordService;
    /**
     * Сервис метрик.
     */
    private final MetricService metricService;

    /**
     * Сервис аудита.
     */
    private final AuditService auditService;
    /**
     * Сканер для чтения пользовательского ввода.
     */
    private final Scanner scanner = new Scanner(System.in);


    /**
     * Конструктор класса.
     *
     * @param context             Контекст аутентификации.
     * @param userService         Сервис пользователей.
     * @param metricRecordService Сервис записей метрик.
     * @param metricService       Сервис метрик.
     * @param auditService        Сервис аудита
     */
    public AppUI(AuthContext context, UserService userService, MetricRecordService metricRecordService,
                 MetricService metricService, AuditService auditService) {
        this.context = context;
        this.userService = userService;
        this.metricRecordService = metricRecordService;
        this.metricService = metricService;
        this.auditService = auditService;
    }


    /**
     * Создает главное меню приложения.
     * Главное меню содержит пункты "Регистрация" и "Авторизация".
     * При выборе пункта "Регистрация" запускается метод {@code registerUserCommand()},
     * при выборе пункта "Авторизация" - метод {@code loginUserCommand()}.
     *
     * @return Объект класса {@code CliMenu}, представляющий главное меню.
     */
    public CliMenu createMainMenu() {
        CliMenu mainMenu = new CliMenu();
        context.logoutUser();

        mainMenu.addItem("Регистрация", this::registerUserCommand);
        mainMenu.addItem("Авторизация", this::loginUserCommand);

        return mainMenu;
    }

    /**
     * Создает подменю пользователя.
     * Подменю содержит пункты "Добавить показания", "Просмотреть свои показания",
     * "Посмотреть показания за определенный месяц" и "Посмотреть актуальные показания за последний месяц".
     * Если пользователь имеет роль "ADMIN", добавляется пункт "Просмотреть все показания".
     *
     * @return Объект класса {@code CliMenu}, представляющий подменю пользователя.
     */
    public CliMenu createSubMenu() {
        CliMenu userMenu = new CliMenu();
        userMenu.addItem("Добавить показания", this::addMetricsCommand);
        userMenu.addItem("Просмотреть свои показания", this::viewUserMetricsCommand);
        userMenu.addItem("Посмотреть показания за определенный месяц", this::viewMetricByMonthCommand);
        userMenu.addItem("Посмотреть актуальные показания за последний месяц", this::viewLastMetricCommand);

        try {
            if (context.getCurrentUserRoles().stream().anyMatch(roles -> roles.toString().equals("ADMIN")))
                userMenu.addItem("Просмотреть все показания", this::viewAllMetricsCommand);
        } catch (MyAuthenticationException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return userMenu;
    }

    /**
     * Метод для обработки команды регистрации нового пользователя.
     * Запрашивает у пользователя имя и пароль, затем регистрирует нового пользователя через сервис пользователей.
     * После успешной регистрации выводит сообщение о успешной операции.
     * Если регистрация не удалась из-за ошибки аутентификации, выводит сообщение об ошибке.
     */
    private void registerUserCommand() {
        auditService.log("Анонимный пользователь пытается зарегестрироваться");

        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        try {
            userService.registrateUser(username, password, Set.of(Role.USER));
            System.out.println("Пользователь зарегистрирован успешно.");
            auditService.log("Пользователь " + username + " зарегистрирован успешно", username);
        } catch (MyAuthenticationException | UserNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Метод для обработки команды аунтификации нового пользователя.
     * Запрашивает у пользователя имя и пароль, затем регистрирует нового пользователя через сервис пользователей.
     * После успешной регистрации выводит сообщение о успешной операции.
     * Если регистрация не удалась из-за ошибки аутентификации, выводит сообщение об ошибке.
     */
    private void loginUserCommand() {
        auditService.log("Анонимный пользователь пытается авторизоваться");

        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        try {
            userService.login(username, password, context);
            String currentUsername = context.getCurrentUsername();
            System.out.println("Авторизация успешна. Добро пожаловать, " + currentUsername + "!");
            auditService.log(String.format("Пользователь %s успешно авторизовался", currentUsername), currentUsername);

            CliMenu userMenu = createSubMenu();
            userMenu.run();

            context.logoutUser();
            auditService.log(String.format("Пользователь %s вышел из аккаунта", currentUsername), currentUsername);


        } catch (MyAuthenticationException | NotFoundException e) {
            System.err.println(e.getMessage());
            auditService.log(String.format("Ошибка при авторизации пользователя %s: %s", username, e.getMessage()));

        }
    }

    /**
     * Обработчик команды добавления показаний метрик пользователем.
     * Запрашивает у пользователя значения для каждой метрики, затем добавляет новую запись метрик
     * через сервис записей метрик.
     * При успешном добавлении выводит сообщение об успешной операции.
     * Если добавление не удалось из-за ошибки аутентификации или некорректных данных,
     * выводит сообщение об ошибке.
     */
    private void addMetricsCommand() {
        String username = "";
        try {
            username = context.getCurrentUsername();

            auditService.log(String.format("Пользователь %s пытается добавить метрики", username), username);
            Map<Metric, Integer> metricsValues = new HashMap<>();

            List<Metric> metrics = metricService.getAllMetric();
            Scanner scanner = new Scanner(System.in);

            for (Metric metric : metrics) {
                System.out.println("Введите показания для метрики: " + metric.getName());
                int val;

                val = Integer.parseInt(scanner.next());

                auditService.log(String.format("Пользователь %s ввел метрику %s: %d", username, metric.getId(), val), username);
                metricsValues.put(metric, val);
            }

            if (metricRecordService.addNewMonthlyMetric(username, metricsValues)) {
                System.out.println("Показания добавлены успешно.");
                auditService.log(String.format("Пользователь %s успешно добавил метрики", username), username);
            } else {
                System.out.println("Ошибка при подаче показаний");
                auditService.log(String.format("Пользователь %s ошибочно ввел метрики", username), username);
            }
        } catch (NotFoundException | MyAuthenticationException e) {
            System.err.println(e.getMessage());
            auditService.log("Ошибка при вводе метрик " + e.getMessage());
        } catch (NumberFormatException e) {
            auditService.log(String.format("Пользователь %s Ввел неверный формат данных", username));
            System.err.println("Введен не верный формат данных, введите числовое показание счетчика");
        }
    }

    /**
     * Вспомогательный метод для отображения списка записей метрик.
     * Выводит на консоль информацию о каждой записи метрик, включая пользователя, дату и значения метрик.
     *
     * @param records Список записей метрик для отображения.
     */
    private void showRecord(List<MetricRecord> records) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        String formattedDateString;

        for (MetricRecord record : records) {
            formattedDateString = record.getMetricDate().format(formatter);
            System.out.printf("\nПользователь: %s, %s", record.getUser().getUsername(), formattedDateString);

            for (Map.Entry<Metric, Integer> entry : record.getMetrics().entrySet()) {
                System.out.printf("\n\t%s: %d ", entry.getKey().getName(), entry.getValue());
            }
        }
        System.out.println("\n");
    }

    /**
     * Обработчик команды просмотра своих метрик пользователем.
     * Выводит на консоль все записи метрик, принадлежащие текущему пользователю.
     * Если записей нет или произошла ошибка аутентификации, выводит соответствующее сообщение об ошибке.
     */
    private void viewUserMetricsCommand() {
        List<MetricRecord> records;

        try {
            String username = context.getCurrentUsername();

            auditService.log(String.format("Пользователь %s запрашивает просмотр своих метрик", username), username);
            records = metricRecordService.getUserMetrics(username);

        } catch (NotFoundException | MyAuthenticationException e) {
            System.err.println(e.getMessage());
            auditService.log(String.format("Ошибка при просмотре пользовательских метрик %s", e.getMessage()));
            return;
        }

        showRecord(records);
    }


    /**
     * Обработчик команды просмотра всех метрик администратором.
     * Выводит на консоль все записи метрик всех пользователей.
     * Если записей нет или произошла ошибка аутентификации, выводит соответствующее сообщение об ошибке.
     */
    private void viewAllMetricsCommand() {
        try {
            auditService.log("Администратор запрашивает просмотр всех метрик", context.getCurrentUsername());
        } catch (UserNotFoundException | MyAuthenticationException e) {
            System.err.println("Ошибка во время просмотра всех метрик администратором");
        }
        List<MetricRecord> records = metricRecordService.getAllUsersMetrics();
        showRecord(records);
    }

    /**
     * Обработчик команды просмотра последней метрики текущего пользователя.
     * Выводит на консоль последнюю запись метрик для текущего пользователя.
     * Если записей нет или произошла ошибка аутентификации, выводит соответствующее сообщение об ошибке.
     */
    private void viewLastMetricCommand() {
        List<MetricRecord> records;

        try {
            String username = context.getCurrentUsername();
            auditService.log(String.format("Пользователь %s запрашивает просмотр актуальной метрики", username), username);
            records = List.of(metricRecordService.getLastMetricRecord(username));
        } catch (NotFoundException | MyAuthenticationException e) {
            System.err.println(e.getMessage());
            auditService.log("Ошибка при просмотре актуальной метрике " + e.getMessage());
            return;
        }

        showRecord(records);
    }

    /**
     * Обработчик команды просмотра метрики за указанный месяц текущего пользователя.
     * Запрашивает у пользователя номер месяца и год, затем выводит на консоль запись метрик
     * для указанного месяца и года.
     * Если записей нет или произошла ошибка аутентификации, выводит соответствующее сообщение об ошибке.
     */
    private void viewMetricByMonthCommand() {

        int year;
        int month;
        String username = "";

        try {
            username = context.getCurrentUsername();
            System.out.print("Введите номер месяца показания: ");
            month = Integer.parseInt(scanner.next());

            if (month > 12 || month < 1) {
                System.out.println("Введите номер месяца (1 - 12)");
                return;
            }

            System.out.print("Введите год показания: ");
            year = Integer.parseInt(scanner.next());

            MetricRecord record;

            auditService.log(String.format("Пользователь %s запрашивает просмотр метрики за %d.%d", username, month, year), username);
            record = metricRecordService.getMetricRecordByMonth(username, month, year);

            showRecord(List.of(record));

        } catch (NotFoundException | MyAuthenticationException e) {
            System.err.println(e.getMessage());
            auditService.log(String.format("Ошибка при просмотре метрики %s", e.getMessage()));
        } catch (NumberFormatException e) {
            auditService.log(String.format("Пользователь %s Ввел неверный формат данных", username));
            System.err.println("Введен не верный формат данных, введите числовое показание счетчика");
        }
    }
}
