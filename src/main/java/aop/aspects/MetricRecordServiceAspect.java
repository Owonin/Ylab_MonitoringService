package aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class MetricRecordServiceAspect {

    @Before("Pointcuts.allGetMetricRecordMethods()")
    public void beforeGettingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.contains("getUserMetrics")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Запрашиваются записи метрик для пользователя %s", username);
        } else if (methodName.contains("getAllUsersMetrics")) {
            System.out.println("Запрашиваются все записи метрик для всех пользователей");
        }else if(methodName.equals("getMetricRecordByMonth")){
            String username = (String) joinPoint.getArgs()[0];
            int month = (int) joinPoint.getArgs()[1];
            int year = (int) joinPoint.getArgs()[2];
            System.out.printf("Запрашиваются записи метрик за последний месяц за %d.%d пользователем %s", year,month,username);
        } else if(methodName.equals("getLastMetricRecord")){
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Запрашиваются записи последней метрики за последний месяц пользователем %s",username);
        }
    }

    @AfterReturning(value = "Pointcuts.allGetMetricRecordMethods()", returning = "result")
    public void afterGettingAdvice(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.contains("getUserMetrics")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Записи метрик для пользователя %s получены", username);
        } else if (methodName.contains("getAllUsersMetrics")) {
            System.out.println("Все записи метрик для всех пользователей получены");
        }else if(methodName.equals("getMetricRecordByMonth")){
            String username = (String) joinPoint.getArgs()[0];
            int month = (int) joinPoint.getArgs()[1];
            int year = (int) joinPoint.getArgs()[2];
            System.out.printf("Запрашиваются записи метрик за последний месяц за %d.%d пользователем %s", year,month,username);
        } else if(methodName.equals("getLastMetricRecord")){
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Запрашиваются записи последней метрики за последний месяц пользователем %s",username);
        }
    }


    @Before("Pointcuts.allAddMetricRecordMethods()")
    public void beforeAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.equals("addNewMonthlyMetric")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Попытка добавления новой записи метрик для пользователя %s", username);
        }
    }

    @AfterReturning("Pointcuts.allAddMetricRecordMethods()")
    public void afterAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.equals("addNewMonthlyMetric")) {
            String username = (String) joinPoint.getArgs()[0];
            System.out.printf("Новая запись метрик для пользователя %s успешно добавлена", username);
        }
    }
}