package aop.aspects;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.MetricRecordServiceImpl.get*(..))")
    public void allGetMetricRecordMethods() {}

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.MetricRecordServiceImpl.add*(..))")
    public void allAddMetricRecordMethods() {}

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.MetricServiceImpl.get*(..))")
    public void allGetMetricMethods() {}

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.MetricServiceImpl.add*(..))")
    public void allAddMetricMethods() {}

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.UserServiceImpl.get*(..))")
    public void allGetUserMethods() {}

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.UserServiceImpl.login(..))")
    public void login() {}

    @Pointcut("within(@aop.annotations.Loggable *) && execution(* service.impl.UserServiceImpl.registrateUser(..))")
    public void registrateUser() {}

}