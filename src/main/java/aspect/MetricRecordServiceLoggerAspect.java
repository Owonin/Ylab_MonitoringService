package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class MetricRecordServiceLoggerAspect {


    @Pointcut("execution(* service.impl.MetricRecordServiceImpl.*(..))")
    public void metricRecordServiceMethods() {
    }

    @Before("metricRecordServiceMethods()")
    public void logMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String arguments = Arrays.toString(joinPoint.getArgs());
        System.out.printf("Calling method %s with arguments %s%n", methodName, arguments);
    }

    @AfterReturning(
            pointcut = "metricRecordServiceMethods()",
            returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.printf("Method %s returned %s", methodName, result);
    }
}
