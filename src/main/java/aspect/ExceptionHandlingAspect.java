package aspect;

import domain.exception.NotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.servlet.http.HttpServletResponse;

@Aspect
public class ExceptionHandlingAspect {

    @Pointcut("execution(* service.impl.MetricRecordServiceImpl.*(..))")
    public void metricRecords() {
    }

    @Around("metricRecords()")
    public Object handleNotFoundException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            System.err.println("SDJADLSAJLDHASLDJSALD");
            return joinPoint.proceed();
        } catch (NotFoundException ex) {
            HttpServletResponse response = getResponse(joinPoint.getArgs());
            if (response != null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                System.err.println("HttpServletResponse is null, unable to set status.");
            }
            return null; // Можно вернуть что-то еще, в зависимости от вашей логики
        }
    }

    private HttpServletResponse getResponse(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof HttpServletResponse) {
                return (HttpServletResponse) arg;
            }
        }
        return null;
    }

}
