package aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class MetricServiceAspect {

    @Before("Pointcuts.allGetMetricMethods()")
    public void beforeGettingAdvice(JoinPoint joinPoint) {
        //todo Audit
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.contains("getAll")) {
            System.out.println("Попытка получения всех метрик");
        } else if (methodName.contains("getMetricsSize")) {
            System.out.println("Попытка получения количества метрик");
        }
    }

    @AfterReturning(value = "Pointcuts.allGetMetricMethods()", returning = "result")
    public void afterGettingAdvice(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.contains("getAll")) {
            System.out.println("Все метрики получены");
        } else if (methodName.contains("getMetricsSize")) {
            System.out.printf("Количество метрик %d получено", (int) result);
        }
    }


    @Before("Pointcuts.allAddMetricMethods()")
    public void beforeAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.equals("addNewMetric")) {
            String metricName = (String) joinPoint.getArgs()[0];
            //todo audit
            System.out.printf("Попытка добавить метрику с названием %s", metricName);
        }
    }

    @AfterReturning("Pointcuts.allAddMetricMethods()")
    public void afterAddingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        if (methodName.equals("addNewMetric")) {
            String metricName = (String) joinPoint.getArgs()[0];
            //todo audit
            System.out.printf("Добавлена метрика с названием %s", metricName);
        }
    }
}