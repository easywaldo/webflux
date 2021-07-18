package com.webflux.webflux;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TimerAop {

    @Pointcut("execution(* com.webflux.webflux.controller..*.*(..))")
    private void pointCut() {
    }

    @Pointcut("@annotation(com.webflux.webflux.annotation.MyTimer)")
    private void enableTimer() {
    }

    @Around("pointCut() && enableTimer()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around aop..");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();

        System.out.println("totalTime :" + stopWatch.getTotalTimeSeconds());
        return result;
    }


}
