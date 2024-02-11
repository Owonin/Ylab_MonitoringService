package aop.aspects;

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
public class MetricServiceAspect {

    private final AuditService auditService;

    /**
     * Конструктор класса
     */
    public MetricServiceAspect() {

        ConfigReader configReader = ConfigReader.getInstance();
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));

        AuditRepository auditRepository = new JdbcAuditRepository(connectionProvider);
        UserRepository userRepository = new JdbcUserRepository(connectionProvider);
        this.auditService = new AuditServiceImpl(auditRepository,userRepository);
    }

    /**
     * Логирование и аудит перед get методами сервиса Metric
     *
     * @param joinPoint
     */
    @Before("Pointcuts.allGetMetricMethods()")
    public void beforeGettingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "";

        if (methodName.contains("getAll")) {
            message = "Попытка получения всех метрик";
        } else if (methodName.contains("getMetricsSize")) {
            message = "Попытка получения количества метрик";
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит после get методами сервиса Metric
     *
     * @param joinPoint
     */
    @AfterReturning(value = "Pointcuts.allGetMetricMethods()", returning = "result")
    public void afterGettingAdvice(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "";

        if (methodName.contains("getAll")) {
            message = "Все метрики получены";
        } else if (methodName.contains("getMetricsSize")) {
            message = String.format("Количество метрик %d получено", (int) result);
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит перед add методами сервиса Metric
     *
     * @param joinPoint
     */
    @Before("Pointcuts.allAddMetricMethods()")
    public void beforeAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "";

        if (methodName.equals("addNewMetric")) {
            String metricName = (String) joinPoint.getArgs()[0];
            message = String.format("Попытка добавить метрику с названием %s", metricName);
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит После add методами сервиса Metric
     *
     * @param joinPoint
     */
    @AfterReturning("Pointcuts.allAddMetricMethods()")
    public void afterAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "";

        if (methodName.equals("addNewMetric")) {
            String metricName = (String) joinPoint.getArgs()[0];
            message = String.format("Добавлена метрика с названием %s", metricName);
        }

        auditService.log(message);
        System.out.println(message);
    }
}