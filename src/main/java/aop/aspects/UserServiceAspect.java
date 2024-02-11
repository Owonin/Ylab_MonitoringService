package aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;


@Aspect
public class UserServiceAspect {

    @Before("Pointcuts.allGetUserMethods()")
    public void beforeGettingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.contains("getAll")) {
            System.out.println("Попытка получения всех пользователей");
        }
    }

    @AfterReturning(value = "Pointcuts.allGetUserMethods()", returning = "result")
    public void afterGettingAdvice(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.contains("getAll")) {
            System.out.println("Данные всех пользователей получены");
        }
    }


    @Before("Pointcuts.registrateUser()")
    public void beforeRegistrateUserAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("registrateUser")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Попытка регистрации пользователя с именем %s", username);
        }
    }

    @AfterReturning("Pointcuts.registrateUser()")
    public void afterRegistrateUserAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("registrateUser")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Пользователь с именем %s успешно зарегистрирован", username);
        }
    }

    @Before("Pointcuts.login()")
    public void beforeLoginAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("login")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Попытка входа пользователя с именем %s", username);
        }
    }

    @AfterReturning("Pointcuts.login()")
    public void afterLoginAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("login")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Пользователь с именем %s успешно вошел в систему", username);
        }
    }
}