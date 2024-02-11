package aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.time.LocalDateTime;

@Aspect
public class LoggableAspect {

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {
    }

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature methodName = proceedingJoinPoint.getSignature();

        System.out.println("Вызов метода " + methodName + " " + LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("Выполнение метода " + methodName +
                " закончено. Время выполнения " + (endTime - startTime) + " мс. " + LocalDateTime.now());
        return result;
    }
}