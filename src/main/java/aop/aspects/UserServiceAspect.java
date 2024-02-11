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
public class UserServiceAspect {

    private final AuditService auditService;

    /**
     * Конструктор класса
     */
    public UserServiceAspect() {

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
     * Логирование и аудит перед get методами сервиса User
     *
     * @param joinPoint
     */
    @Before("Pointcuts.allGetUserMethods()")
    public void beforeGettingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "Неожиданная операция";

        if (methodName.contains("getAll")) {
            message = "Попытка получения всех пользователей";
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит после get методами сервиса User
     *
     * @param joinPoint
     */
    @AfterReturning(value = "Pointcuts.allGetUserMethods()", returning = "result")
    public void afterGettingAdvice(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String message = "Неожиданная операция";

        if (methodName.contains("getAll")) {
            message = "Данные всех пользователей получены";
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит перед регистрированием пользователя
     *
     * @param joinPoint
     */
    @Before("Pointcuts.registrateUser()")
    public void beforeRegistrateUserAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String message = "Неожиданная операция";

        if (methodName.equals("registrateUser")) {
            String username = (String) joinPoint.getArgs()[0];
            message = String.format("Попытка регистрации пользователя с именем %s", username);
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит после регестрации пользователя
     *
     * @param joinPoint
     */
    @AfterReturning("Pointcuts.registrateUser()")
    public void afterRegistrateUserAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String message = "Неожиданная операция";

        if (methodName.equals("registrateUser")) {
            String username = (String) joinPoint.getArgs()[0];
            message = String.format("Пользователь с именем %s успешно зарегистрирован", username);
        }

        auditService.log(message);
        System.out.println(message);
    }

    /**
     * Логирование и аудит перед входа в аккаунт
     *
     * @param joinPoint
     */
    @Before("Pointcuts.login()")
    public void beforeLoginAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String message = "Неожиданная операция";

        if (methodName.equals("login")) {
            String username = (String) joinPoint.getArgs()[0];
            message = String.format("Попытка входа пользователя с именем %s", username);
        }

        auditService.log(message);
        System.out.println(message);
    }


    /**
     * Логирование и аудит после входа в аккаунт
     *
     * @param joinPoint
     */
    @AfterReturning("Pointcuts.login()")
    public void afterLoginAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String message = "Неожиданная операция";

        if (methodName.equals("login")) {
            String username = (String) joinPoint.getArgs()[0];
            message = String.format("Пользователь с именем %s успешно вошел в систему", username);
        }

        auditService.log(message);
        System.out.println(message);
    }
}