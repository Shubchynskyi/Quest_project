package com.javarush.quest.shubchynskyi.config.aspects;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class TxAspectProcessor {
    private final SessionCreator sessionCreator;

    @Around("TxAspectPointcut.isMethodAnnotation()")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        sessionCreator.beginTransactional();
        Object result;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            sessionCreator.endTransactional();
        }
    }
}
