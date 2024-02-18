package com.example.aop.aspects;

import com.example.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Aspect
@Component
@RequiredArgsConstructor
public class LoggableAspect {

    private final AuditService auditService;

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {
    }

    /**
     * Добавляет возможность логирования методов и выводит время работы метода
     *
     * @param proceedingJoinPoint JoinPoint
     * @return
     * @throws Throwable
     */
    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature methodName = proceedingJoinPoint.getSignature();

        String message = "Вызов метода " + methodName + " " + LocalDateTime.now();
        auditService.log(message);
        System.out.println(message);

        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();

        message = "Выполнение метода " + methodName +
                " закончено. Время выполнения " + (endTime - startTime) + " мс. " + LocalDateTime.now();
        System.out.println(message);
        auditService.log(message);

        return result;
    }
}