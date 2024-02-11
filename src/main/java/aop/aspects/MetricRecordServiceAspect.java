package aop.aspects;

import domain.exception.UserNotFoundException;
import domain.repository.AuditRepository;
import domain.repository.UserRepository;
import domain.repository.jdbc.JdbcAuditRepository;
import domain.repository.jdbc.JdbcUserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import service.AuditService;
import service.impl.AuditServiceImpl;
import util.ConfigReader;
import util.DBConnectionProvider;

@Aspect
public class MetricRecordServiceAspect {

    private final AuditService auditService;

    public MetricRecordServiceAspect() {

        ConfigReader configReader = ConfigReader.getInstance();
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));

        AuditRepository auditRepository = new JdbcAuditRepository(connectionProvider);
        UserRepository userRepository = new JdbcUserRepository(connectionProvider);
        this.auditService = new AuditServiceImpl(auditRepository,userRepository);
    }

    @Before("Pointcuts.allGetMetricRecordMethods()")
    public void beforeGettingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "Неожиданная операция";
        String username = null;

        if (methodName.contains("getUserMetrics")) {
            username = (String) joinPoint.getArgs()[0];
            message = String.format("Запрашиваются записи метрик для пользователя %s", username);
        } else if (methodName.contains("getAllUsersMetrics")) {
            message = "Запрашиваются все записи метрик для всех пользователей";
        } else if (methodName.equals("getMetricRecordByMonth")) {
            username = (String) joinPoint.getArgs()[0];
            int month = (int) joinPoint.getArgs()[1];
            int year = (int) joinPoint.getArgs()[2];
            message = String.format("Запрашиваются записи метрик за %d.%d пользователем %s", year, month, username);
        } else if (methodName.equals("getLastMetricRecord")) {
            username = (String) joinPoint.getArgs()[0];
            message = String.format("Запрашиваются записи последней метрики за последний месяц пользователем %s", username);
        }

        try {
            auditService.log(message, username);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
    }

    @AfterReturning(value = "Pointcuts.allGetMetricRecordMethods()", returning = "result")
    public void afterGettingAdvice(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "Неожиданная операция";
        String username = null;

        if (methodName.contains("getUserMetrics")) {
            username = (String) joinPoint.getArgs()[0];
            message = String.format("Записи метрик для пользователя %s получены", username);
        } else if (methodName.contains("getAllUsersMetrics")) {
            message = "Все записи метрик для всех пользователей получены";
        } else if (methodName.equals("getMetricRecordByMonth")) {
            username = (String) joinPoint.getArgs()[0];
            int month = (int) joinPoint.getArgs()[1];
            int year = (int) joinPoint.getArgs()[2];
            message = String.format("Запрашиваются записи метрик за последний месяц за %d.%d пользователем %s", year, month, username);
        } else if (methodName.equals("getLastMetricRecord")) {
            username = (String) joinPoint.getArgs()[0];
            message = String.format("Запрашиваются записи последней метрики за последний месяц пользователем %s", username);
        }

        try {
            auditService.log(message, username);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
    }

    @Before("Pointcuts.allAddMetricRecordMethods()")
    public void beforeAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "Неожиданная операция";
        String username = null;

        if (methodName.equals("addNewMonthlyMetric")) {
            username = (String) joinPoint.getArgs()[0];
            message = String.format("Попытка добавления новой записи метрик для пользователя %s", username);
        }

        try {
            auditService.log(message, username);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
    }

    @AfterReturning("Pointcuts.allAddMetricRecordMethods()")
    public void afterAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "Неожиданная операция";
        String username = null;

        if (methodName.equals("addNewMonthlyMetric")) {
            username = (String) joinPoint.getArgs()[0];
            message = String.format("Новая запись метрик для пользователя %s успешно добавлена", username);
        }


        try {
            auditService.log(message, username);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
    }
}