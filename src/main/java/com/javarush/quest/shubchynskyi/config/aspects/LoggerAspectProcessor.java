package com.javarush.quest.shubchynskyi.config.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspectProcessor {
    long time;

    @Before("LoggerAspectPointcut.isMethodAnnotation()")
    public void beforeMethodLoggerAnnotation(JoinPoint point) {
        time = System.nanoTime();
    }

    @After("LoggerAspectPointcut.isMethodAnnotation()")
    public void afterMethodLoggerAnnotation(JoinPoint point) {
        System.err.printf("Method %s done for %s ms\n", point.toShortString(), (System.nanoTime() - time)/1000000);
    }
}
